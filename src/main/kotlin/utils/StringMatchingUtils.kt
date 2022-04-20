package utils

fun String.appendStringByMatch(match: String, append: String, indentation: String): String =
    appendStringByMatch(match, append, append, indentation)

fun String.appendStringByMatch(
    match: String,
    appendId: String,
    append: String,
    indentation: String
): String {
    val appendIdRegex = appendId.toRegex()
    val appendIdMatch = appendIdRegex.find(this)?.range

    return if (appendIdMatch == null) {
        val matchRegex = match.toRegex()
        val regexMatch = matchRegex.find(this)?.range
        if (regexMatch != null) {
            "${this.substring(0, regexMatch.last + 1)}$indentation$append${
                this.substring(
                    regexMatch.last + 1, 
                    this.length
                )
            }"
        } else {
            this
        }
    } else {
        this
    }
}
