package yukitas.animal.collector.utility

import android.text.Editable

fun tagsFromText(tags: Editable): List<String> {
    return tags.split("\\s+".toRegex()).map { it.trim() }
}