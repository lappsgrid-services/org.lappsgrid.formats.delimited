package org.lappsgrid.formats.delimited

import org.junit.Ignore
import org.junit.Test
import org.lappsgrid.formats.delimited.extra.Window
import org.lappsgrid.serialization.lif.Annotation

/**
 *
 */
@Ignore
class WindowTest {


    @Test
    void sizes() {
        Window w = new Window(1, ",")
        assert w.size() == 1
        assert w.length() == 0
        assert w.capacity() == 3
        w = new Window(2, ",")
        assert w.size() == 2
        assert w.length() == 0
        assert w.capacity() == 5
    }

    @Test
    void defaults() {
        Window w = new Window()
        assert 1 == w.size()
        assert 0 == w.length()
        assert 3 == w.capacity()

        w.add(annotation("-"))
        w.add(annotation("1"))
        w.add(annotation("2"))
        w.add(annotation("-"))

        assert w.length() == 3
        String s = w.toString()
        List<String> lines = s.readLines()
        assert 1 == lines.size()
        assert "2,2,2,1,1,1,-,-,-" == lines[0]
    }

    Annotation annotation(String id) {
        Annotation a = new Annotation()
        a.features.word = id
        a.features.lemma = id
        a.features.pos = id
        return a
    }
}
