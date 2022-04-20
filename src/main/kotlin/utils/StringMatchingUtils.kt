package utils

fun String.appendStringByMatch(match: String, append: String, indentation: String): String =
    appendStringByMatch(match, append, append, indentation)

/**
 * Injects a String into a receiver String of content. Before adding it, we check if it will be a duplicate.
 * We also use another substring in the receiver as the anchor location where we add our new String.
 *
 * @receiver The String content into which [append] is being injected.
 *
 * @param match The substring we will search for in the receiver content and add [append] to the end of this.
 * @param appendId If this substring exists in the receiver content already, we won't add [append].
 * @param append The String we want to add to the receiver content.
 * @param indentation The indentation to add between [match] and [append].
 * @return String with [append] injected into it, or the same string if no changes are made.
 */
fun String.appendStringByMatch(
    match: String,
    appendId: String,
    append: String,
    indentation: String
): String {
    val appendIdRegex = appendId.toRegex()
    val appendIdMatch = appendIdRegex.find(this)?.range

    return if (appendIdMatch == null) {
        // no duplicate found, continue with logic to add the new string
        val matchRegex = match.toRegex()
        val regexMatch = matchRegex.find(this)?.range
        if (regexMatch != null) {
            // anchor substring found, add the new string to the end of it
            "${this.substring(0, regexMatch.last + 1)}$indentation$append${
                this.substring(
                    regexMatch.last + 1,
                    this.length
                )
            }"
        } else {
            // anchor substring not found, don't add new string
            // TODO: track error, as we SHOULD be injecting the string
            this
        }
    } else {
        // duplicate substring found, don't add new string
        this
    }
}
