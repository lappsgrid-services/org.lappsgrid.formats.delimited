package org.lappsgrid.formats.delimited

/**
 *
 */
class Dictionary {
    Map<String,Integer> dictionary = new HashMap<>()

    Dictionary(List<String> items) {
        items.eachWithIndex{ String entry, int i ->
            dictionary.put(entry, i)
        }
    }

    int size() {
        return dictionary.size()
    }

    int lookup(String token) {
        Integer i = dictionary.get(token)
        if (i == null) {
            i = dictionary.size()
            dictionary.put(token, i)
        }
        return i
    }
}
