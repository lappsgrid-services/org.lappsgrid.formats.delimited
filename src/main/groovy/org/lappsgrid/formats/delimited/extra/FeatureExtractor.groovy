package org.lappsgrid.formats.delimited.extra

import org.lappsgrid.serialization.lif.Annotation

/**
 * Returns a feature value from an annotation.
 */
class FeatureExtractor {
    String name

    FeatureExtractor(String name) {
        this.name = name
    }

    String extract(Annotation a) {
        String value = a.features[name]
        if (value == null) {
            return "-"
        }
        return value
    }
}
