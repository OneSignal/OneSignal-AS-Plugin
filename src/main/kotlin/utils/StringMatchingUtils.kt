package utils

fun String.appendStringByMatch(match: String, append: String, indentation: String): String {
    val appendRegex = append.toRegex()
    val appendMatch = appendRegex.find(this)?.range

    return if (appendMatch == null) {
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
