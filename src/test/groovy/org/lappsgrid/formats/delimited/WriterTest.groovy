package org.lappsgrid.formats.delimited

import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.lappsgrid.formats.delimited.extra.FeatureExtractor
import org.lappsgrid.vocabulary.Features

import static org.lappsgrid.discriminator.Discriminators.Uri
import org.lappsgrid.serialization.Data
import org.lappsgrid.serialization.Serializer
import org.lappsgrid.serialization.lif.Annotation
import org.lappsgrid.serialization.lif.Container
import org.lappsgrid.serialization.lif.View

/**
 *
 */
@Ignore
class WriterTest {

    @Test
    void inception() {
        InputStream stream = this.class.getResourceAsStream("/inception.lif")
        Data data = Serializer.parse(stream.text)
        Container container = new Container(data.payload)
        Writer writer = new Writer()
        println writer.process(container)
    }

    @Ignore
    void rewrite() {
        InputStream stream = this.class.getResourceAsStream("/inception.lif")
        Data data = Serializer.parse(stream.text)
        println data.asPrettyJson()
    }

    void size3() {
        InputStream stream = this.class.getResourceAsStream("/lemmatized.lif")
        Data data = Serializer.parse(stream.text)
        Container container = new Container(data.payload)
        int three = 3
        Writer writer = new Writer(three)
        println writer.process(container)
    }

    void mergeNER() {
        InputStream stream = this.class.getResourceAsStream("/karen-ner.lif")
        Data data = Serializer.parse(stream.text)
        Container container = new Container(data.payload)

        /*
        List<Annotation> tokens = findAnnotations(container, Uri.TOKEN)
        List<Annotation> entities = findAnnotations(container, Uri.NE)
        for (Annotation entity : entities) {
            List<Annotation> words = findConveredAnnotations(tokens, entity)
            if (words.size() == 1) {
                words[0].features[Features.NamedEntity.CATEGORY] = entity.features[Features.NamedEntity.CATEGORY]
            }
            else {
                words.eachWithIndex{ Annotation entry, int i ->
                    String cat = entity.features[Features.NamedEntity.CATEGORY] + "_$i"
                    entry.features[Features.NamedEntity.CATEGORY] = cat
                }
            }
        }
        */
//        data.payload = container
//        println data.asPrettyJson()
        Writer writer = new Writer(1, '\t')
        println writer.process(container)
    }

    List<Annotation> findConveredAnnotations(List<Annotation> spans, Annotation a) {
        return spans.findAll { a.start <= it.start && it.end <= a.end }
    }

    List<Annotation> findAnnotations(Container container, String type) {
        List<View> views = container.findViewsThatContain(type)
        if (views.size() == 0) {
            return []
        }
        View view = views[-1]
        return view.annotations.findAll { it.atType == type }
    }

}
