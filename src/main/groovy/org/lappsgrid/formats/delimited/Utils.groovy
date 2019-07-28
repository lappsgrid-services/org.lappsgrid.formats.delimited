package org.lappsgrid.formats.delimited

import org.lappsgrid.serialization.lif.Annotation
import org.lappsgrid.serialization.lif.Container
import org.lappsgrid.serialization.lif.View

/**
 *
 */
class Utils {
    static List<Annotation> findConveredAnnotations(List<Annotation> spans, Annotation a) {
        return spans.findAll { a.start <= it.start && it.end <= a.end }
    }

    static List<Annotation> findAnnotations(Container container, String type) {
        List<View> views = container.findViewsThatContain(type)
        if (views.size() == 0) {
            return findAnnotations2(container, type)
        }
        View view = views[-1]
        return view.annotations.findAll { it.atType == type }
    }

    String getWord(Container container, Annotation token) {
        if (token.features && token.features.word) {
            return token.features.word
        }
        int start = token.start.intValue()
        int end = token.end.intValue()
        return container.text.substring(start, end)
    }

    List<Annotation> findAnnotations2(Container container, String type) {
        for (View view : container.views) {
            for (Annotation a : view.annotations) {
                if (a.atType == type) {
                    return view.annotations.findAll{ it.atType == type }
                }
            }
        }
        return []
    }
    protected Utils() {}
}
