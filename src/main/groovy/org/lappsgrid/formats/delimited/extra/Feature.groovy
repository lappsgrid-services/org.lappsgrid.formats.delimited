package org.lappsgrid.formats.delimited.extra

import org.lappsgrid.discriminator.Discriminators

/**
 * A feature to be extracted from an annotation.
 */
class Feature {
    /** The annotation type that contains the feature. */
    String type
    /** The feature name. */
    String name

    Feature() { }
    Feature(String type, String name) {
        this.type = type
        this.name = name
    }

    Feature(String name) {
        this.type = Discriminators.Uri.TOKEN
        this.name = name
    }

    String toString() {
        return "${type}#${name}"
    }

}
