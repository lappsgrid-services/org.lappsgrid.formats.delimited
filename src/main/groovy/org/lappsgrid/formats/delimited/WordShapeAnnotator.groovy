package org.lappsgrid.formats.delimited

import groovy.util.logging.Slf4j
import org.lappsgrid.serialization.lif.Annotation
import org.lappsgrid.serialization.lif.Container
import static org.lappsgrid.discriminator.Discriminators.Uri

/**
 *
 */
@Slf4j("logger")
class WordShapeAnnotator {

    void process(Container container) {
        String text = container.text
        logger.info("Processing container size: {}", text.length())
        List<Annotation> tokens = Utils.findAnnotations(container, Uri.TOKEN)
        int count = 0
        tokens.each { Annotation token ->
            if (token.features.shape == null) {
                String word = string(text, token)
                String shape = "mixed"
                int uc = countUppercase(word)
                if (uc == 0) {
                    if (isPunct(word)) {
                        shape = "punct"
                    }
                    else {
                        shape = "lower"
                    }
                }
                else if (uc == 1) {
                    if (isUpper(word.chars[0])) {
                        shape="capitalized"
                    }
                }
                else if (uc == word.length()) {
                    shape = "upper"
                }
                token.features.shape = shape
                ++count
            }
        }
        logger.debug("Added {} shape features", count)
    }

    boolean isUpper(char ch) {
        return Character.isUpperCase(ch)
    }

    boolean isPunct(String word) {
        if (word.length() > 1) {
            return false
        }
        char ch = word.charAt(0)
        return !Character.isLetterOrDigit(ch)
    }

    int countUppercase(String word) {
        return word.chars.findAll{ ch -> isUpper(ch) }.size()
    }

    String string(String text, Annotation a) {
        int start = a.start.intValue()
        int end = a.end.intValue()
        try {
            return text.substring(start, end)
        }
        catch (Exception e) {
            return "-"
        }
    }
}
