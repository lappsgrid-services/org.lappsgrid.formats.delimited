package org.lappsgrid.formats.delimited

import org.junit.Ignore
import org.junit.Test
import org.lappsgrid.serialization.lif.Annotation

import static org.lappsgrid.discriminator.Discriminators.Uri
import org.lappsgrid.serialization.Data
import org.lappsgrid.serialization.Serializer
import org.lappsgrid.serialization.lif.Container
import org.lappsgrid.serialization.lif.View

/**
 *
 */
class ScratchPad {

    @Ignore
    void sublist() {
        List items = [0,1,2,3,4,5,6,7]
        int cutoff = (items.size() * 0.8).intValue()
        Collections.shuffle(items)
        println items.size()
        println cutoff
        println items[0..cutoff-1]
        println items[cutoff..-1]
    }

    void partition(String has_header, String shuffle, File infile, Float percent, File train, File test) {
//        String header = args[0]
//        File infile = new File(args[1])
//        String percent = args[2]
//        File train = new File(args[3])
//        File test = new File(args[4])

        String header = null;
        List<String> lines = infile.readLines()
        if (header == "yes") {
            header = lines.remove(0)
        }
        if (shuffle == "yes") {
            Collections.shuffle(lines)
        }
        if (percent > 1.0) {
            percent = percent / 100.0f
        }
        int cutoff = (lines.size() * percent).intValue()

        train.text = lines[0..cutoff-1]
        test.text = lines[cutoff..-1]
    }

    @Test
    void dictionary() {
        Set<String> dictionary = new HashSet<String>()
        File input = new File("/tmp/files.txt")
        File output = new File("/tmp/dictionary.txt")
        input.eachLine { String path ->
            File file = new File(path)
            if (file.exists()) {
                println "Processing ${file.path}"
                Data data = Serializer.parse(file.text)
                Container container = new Container(data.payload)
                List<Annotation> annotations = find(container, Uri.TOKEN)
                annotations.each { Annotation token ->
                    String value = word(container, token)
                    dictionary.add(word)
                    if (token.features.pos) {
                        dictionary.add(token.features.pos)
                    }
                    if (token.features.lemma) {
                        dictionary.add(token.features.lemma)
                    }
                }
                annotations = find(container, Uri.NE)
                annotations.each { Annotation entity ->
                    if (entity.features.category) {
                        dictionary.add(entity.features.category)
                    }
                    else if (entity.label) {
                        dictionary.add(entity.label)
                    }
                }
            }
        }
        println "Writing dictionary. Size: ${dictionary.size()}"
        output.withWriter("UTF-8") { writer ->
            dictionary.sort().each { String word ->
                writer.writeLine(word)
            }
        }
    }

    List<Annotation> find(Container container, String type) {
        List<View> views = container.findViewsThatContain(type)
        if (views.size() == 0) {
            return []
        }
        return views[-1].findByAtType(type)
    }

    String word(Container container, Annotation a) {
        if (a.features.word) {
            return a.features.word
        }
        int start = a.start.intValue()
        int end = a.end.intValue()
        return container.text.substring(start, end)
    }
}
