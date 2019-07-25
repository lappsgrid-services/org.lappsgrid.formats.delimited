package org.lappsgrid.formats.delimited.extra

import org.lappsgrid.serialization.lif.Annotation

/**
 *
 */
class Window {
    FeatureExtractor[] extractors
    List<Annotation> window
    final int size
    final int length
    final String separator
    String _string

    Window(FeatureExtractor[] extractors, int size = 1, String separator=",") {
        this.extractors = extractors
        this.size = size
        this.length = size + size + 1
        window = new ArrayList<Annotation>(length);
        this.separator = separator
    }

    int size() { return size }
    int capacity() { return length }
    int length() { return window.size() }

    void add(Annotation a) {
//        println "Window adding ${a.features.word}"
        window.add(a)
        if (window.size() > length) {
//            println "Removing front of the window"
            window.remove(0)
        }
        _string = null
    }

    String toString() {
        if (_string != null) {
            return _string
        }
        String[] features = new String[length*extractors.length]
        int index = addFeatures(window[size], features, 0)
        for (int i = 0; i < size; ++i) {
            index = addFeatures(window[i], features, index)
        }
        for (int i = size+1; i < length; ++i) {
            index = addFeatures(window[i], features, index)
        }
        _string = features.join(separator)
        return _string
    }

    int addFeatures(Annotation token, String[] features, int index) {
        extractors.each { FeatureExtractor fe ->
            features[index++] = fe.extract(token)

        }
//        features[index++] = token.features.word
//        features[index++] = token.features.lemma
//        features[index++] = token.features.pos

        return index
    }

    static void main(String[] args) {
        int[] a = [1,2,3]
        int[] b = [4,5,6]
        int[] c = [7,8,9]

        int[] ab = a + b
        println ab.join(",")
        int[] abc = ab + c
        println abc.join(",")
    }
}
