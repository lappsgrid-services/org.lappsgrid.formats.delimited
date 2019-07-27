package org.lappsgrid.formats.delimited

import org.junit.BeforeClass
import org.junit.Ignore
import org.junit.Test
import org.lappsgrid.rabbitmq.Message
import org.lappsgrid.rabbitmq.topic.MessageBox
import org.lappsgrid.rabbitmq.topic.PostOffice
import org.lappsgrid.serialization.Data
import org.lappsgrid.serialization.Serializer

/**
 *
 */
@Ignore
class ServiceTest {

    static Config config

    @BeforeClass
    static void setupClass() {
        config = new Config()
        System.setProperty("RABBIT_USERNAME", config.USERNAME)
        System.setProperty("RABBIT_PASSWORD", config.PASSWORD)

    }

    @Test
    void test() {
        Object lock = new Object()
        PostOffice po = new PostOffice(config.EXCHANGE, config.RABBIT_HOST)
//        DelimitedFileWorker service =
        new DelimitedFileWorker()
//        sleep(500)

        String mailbox = UUID.randomUUID().toString()
        MessageBox box = new MessageBox(config.EXCHANGE, mailbox, config.RABBIT_HOST) {
            @Override
            void recv(Message message) {
                println "Received the response"
                Data data = Serializer.parse(message.body)
                println data.payload
                synchronized (lock) {
                    lock.notifyAll()
                }
            }
        }

        String settings = Serializer.toJson([ size:2, separator:'\t'])

        InputStream stream = this.class.getResourceAsStream("/karen-ner.lif")
        Message message = new Message()
                .command(settings)
                .body(stream.text)
                .route(config.MAILBOX)
                .route(mailbox)

        println "Sending the message"
        po.send(message)
        println "Waiting for the response"
        synchronized (lock) {
            lock.wait(30000)
        }
        println "Sending QUIT message"
        Message m = new Message()
        m.body("");
        m.command("QUIT");
        m.route(config.MAILBOX)
        po.send(m)

        println "Done."
    }
}
