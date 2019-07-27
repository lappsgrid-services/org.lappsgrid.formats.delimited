package org.lappsgrid.formats.delimited

import groovy.util.logging.Slf4j
import org.lappsgrid.rabbitmq.topic.MessageBox
import org.lappsgrid.rabbitmq.topic.PostOffice

import org.lappsgrid.serialization.Data
import org.lappsgrid.serialization.Serializer
import org.lappsgrid.serialization.lif.Annotation
import org.lappsgrid.serialization.lif.Container
import org.lappsgrid.rabbitmq.Message

import static org.lappsgrid.discriminator.Discriminators.Uri

/**
 *
 */
@Slf4j("logger")
class DelimitedFileWorker extends MessageBox {

    static Config config
    static {
        config = new Config()
        System.setProperty("RABBIT_USERNAME", config.USERNAME)
        System.setProperty("RABBIT_PASSWORD", config.PASSWORD)
    }

    WordShapeAnnotator annotator
    PostOffice po
    Object lock

    DelimitedFileWorker() {
        super(config.EXCHANGE, config.MAILBOX, config.RABBIT_HOST)
        annotator = new WordShapeAnnotator()
        po = new PostOffice(config.EXCHANGE, config.RABBIT_HOST)
        logger.info("Started the DelimitedFileWorker service.")
        logger.debug("Host: {}", config.RABBIT_HOST)
        logger.debug("Exchange: {}", config.EXCHANGE)
        logger.debug("Mailbox: {}", config.MAILBOX)
        lock = new Object()
        synchronized (lock) {
            lock.wait()
        }
        po.close()
        this.close()
    }

    void recv(Message message) {
        if (message.command == "EXIT" || message.command == "QUIT") {
            logger.info("Received {} message", message.command)
            synchronized (lock) {
                lock.notifyAll()
            }
            return
        }

        int size = 2
        String sep = "\t"
        if (message.command && message.command.trim().startsWith("{")) {
            Map map = Serializer.parse(message.command, HashMap)
            if (map['size']) {
                size = map['size']
            }
            if (map.separator) {
                sep = map.separator
            }
        }

        Data data = Serializer.parse(message.body)
        Container container = new Container(data.payload)
        annotator.process(container)
        Writer writer = new Writer(size, sep)
        String tsv = writer.process(container)
        data = new Data(Uri.TSV, tsv)
        message.body(data.asJson())
        po.send(message)
    }

    void merge(Container container, String type, String feature) {
        if (type == Uri.TOKEN) {
            return
        }

        List<Annotation> tokens = Utils.findAnnotations(container, Uri.TOKEN)
        List<Annotation> entities = Utils.findAnnotations(container, type)
        for (Annotation entity : entities) {
            List<Annotation> words = Utils.findConveredAnnotations(tokens, entity)
            if (words.size() == 1) {
                words[0].features[feature] = entity.features[feature]
            }
            else {
                words.eachWithIndex{ Annotation entry, int i ->
                    String cat = entity.features[feature] + "_$i"
                    entry.features[feature] = cat
                }
            }
        }
    }

    void run() {
        InputStream stream = this.class.getResourceAsStream("/karen-ner.lif")
        Message message = new Message().body(stream.text)
//        List<Feature> features = [
//                new Feature(Features.Token.WORD),
//                new Feature(Features.Token.LEMMA),
//                new Feature(Features.Token.POS),
//                new Feature("shape")
//        ]
//        Config config = new Config()
//        config.entity = new Feature(Uri.NE, Features.NamedEntity.CATEGORY)
//        config.features = features
//        message.command(Serializer.toJson(config))
        recv(message)
    }

    void test() {
        InputStream stream = this.class.getResourceAsStream("/karen-ner.lif")
        Message message = new Message().body(stream.text)
        String json = Serializer.toPrettyJson(message)

        Message m = Serializer.parse(json, Message)

    }

    static void main(String[] args) {
        new DelimitedFileWorker() //.run()
    }
}
