package org.lappsgrid.formats.delimited

import org.lappsgrid.formats.delimited.extra.FeatureExtractor
import org.lappsgrid.formats.delimited.extra.Window
import org.lappsgrid.serialization.lif.Annotation
import org.lappsgrid.serialization.lif.Container
import org.lappsgrid.serialization.lif.View

import static org.lappsgrid.discriminator.Discriminators.Uri

/**
 *
 */
class Writer {

    private String sep
    private int windowSize
    private String docId

    Writer(int windowSize=1, String sep=',') {
        this.sep = sep
        this.windowSize = windowSize
    }

    String process(Container container) {
        docId = container.metadata.id ?: container.metadata.docId ?: UUID.randomUUID().toString()
        StringWriter string = new StringWriter()
        PrintWriter writer = new PrintWriter(string)

        Annotation dummy = dummy()
        List<Annotation> annotations = combine(container)
        int length = annotations.size() + windowSize
        // Pad the front and back of the annotations list to simplify
        // processing the ends of the list.
        windowSize.times {
            annotations.add(0, dummy)
            annotations.add(dummy)
        }

        // Print the header row.
        writer.print("ENTITY\tSTART\tEND\tID\tPOS\tSHAPE")
        for (int i = windowSize; i > 0; --i) {
            writer.print("\tWORD-$i\tPOS-$i\tSHAPE-$i")
        }
        for (int i = 1; i <= windowSize; ++i) {
            writer.print("\tWORD+$i\tPOS+$i\tSHAPE+$i")
        }
        writer.println("\tCATEGORY")
        for (int i = windowSize; i < length; ++i) {
            Annotation a = annotations[i]
            String cat = a.features.category
            if (cat != null) {
                List strings = []
                strings.add(print(a))
                for (int j = i-windowSize; j < i; ++j)  {
                    strings.add(print(annotations[j]))
                }
                for (int j = i+1; j < i+windowSize+1; ++j) {
                    strings.add(print(annotations[j]))
                }
                strings.add(cat)
                writer.println(strings.join(sep))
            }
        }
        return string.toString()

    }

    String print(Annotation a) {
        return [ a.features.word, a.start, a.end, docId, a.features.pos, a.features.shape ].join(sep)
    }

    List<Annotation> combine(Container container) {
        List<Annotation> result = []
        List<Annotation> tokenList = Utils.findAnnotations(container, Uri.TOKEN)
        List<Annotation> entityList = Utils.findAnnotations(container, Uri.NE)
        if (tokenList.size() == 0) {
            return entityList
        }
        if (entityList.size() == 0) {
            return tokenList
        }
        Iterator<Annotation> tokens = tokenList.iterator()
        Iterator<Annotation> entities = entityList.iterator()
        Annotation token = tokens.next()
        Annotation entity = entities.next()
        boolean merging = true
        while (merging) {
            if (token.start < entity.start) {
                // This token comes before the entity.
                result.add(token)
                if (tokens.hasNext()) {
                    token = tokens.next()
                }
                else {
                    merging = false
                }
            }
            else {
                result.add(entity)
                // Skip over tokens covered by this entity. Most entities will be
                // single tokens so we handle the first token as a special case.
                int start = token.start.intValue()
                int end = token.end.intValue()
                if (tokens.hasNext() && token.start < entity.end) {
                    entity.features.pos = token.features.pos
                    entity.features.shape = token.features.shape
                    token = tokens.next()
                }
                // Now process the rest of the tokens.
                while (tokens.hasNext() && token.start < entity.end) {
                    entity.features.pos += "_" + token.features.pos
                    entity.features.shape += "_" + token.features.shape
                    end = token.end.intValue()
                    token = tokens.next()
                }
                entity.features.word = container.text.substring(start, end)
                // Get the next entity if there is one.
                if (entities.hasNext()) {
                    entity = entities.next()
                }
                else {
                    merging = false
                }
            }
        }
//        while (entities.hasNext()) {
//            result.add(entities.next())
//        }
        result.add(token)
        while (tokens.hasNext()) {
            result.add(tokens.next())
        }
        return result
    }


    Annotation dummy() {
        Annotation a = new Annotation()
        a.atType = "dummy"
        a.id = "dummy"
        a.start = 0
        a.end = 0
        a.features.word = "-"
        a.features.lemma = "-"
        a.features.pos="-"
        a.features.category="-"
        a.features.shape="-"
        return a
    }

    /*
    List<Annotation> findAnnotations(Container container, String type) {
        List<View> views = container.findViewsThatContain(type)
        if (views.size() == 0) {
            return []
        }
        View view = views[-1]
        return view.annotations.findAll { it.atType == type }
    }

    List<Annotation> findAnnotations(Container container, String type, Annotation span) {
        List<View> views = container.findViewsThatContain(type)
        if (views.size() == 0) {
            return []
        }
        View view = views[-1]
        return view.annotations.findAll { it.atType == type && span.start <= it.start && it.end <= span.end }
    }
    */
}
