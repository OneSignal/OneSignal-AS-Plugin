package utils

import com.intellij.notification.NotificationDisplayType
import com.intellij.notification.NotificationGroup
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project

fun String.appendStringByMatch(match: String, append: String, indentation: String, project: Project): String =
    appendStringByMatch(match, append, append, indentation, project)

fun String.appendStringByMatch(
    match: String,
    appendId: String,
    append: String,
    indentation: String,
    project: Project
): String {
    val appendIdRegex = appendId.toRegex()
    val appendIdMatch = appendIdRegex.find(this)?.range

    showNotification(project, "appendId string: $appendId match: $appendIdMatch")
    return if (appendIdMatch == null) {
        val matchRegex = match.toRegex()
        val regexMatch = matchRegex.find(this)?.range
        showNotification(project, "Match string: $match match: $regexMatch")
        if (regexMatch != null) {
            showNotification(project, "Making replacement \"$match$indentation$append\"")
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

/**
 * Show a notification to aid in debug process while working
 */
fun showNotification(project: Project, message: String) {
    NotificationGroup("someID", NotificationDisplayType.BALLOON)
        .createNotification(
            "OneSignal plugin:",
            message,
            NotificationType.WARNING,
            null
        ).notify(project)
}