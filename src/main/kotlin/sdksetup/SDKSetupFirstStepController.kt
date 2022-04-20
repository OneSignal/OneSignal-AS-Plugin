package sdksetup

import com.intellij.openapi.project.Project
import utils.appendStringByMatch
import java.io.File

class SDKSetupFirstStepController {
    /**
     * Add dependencies to project level build.gradle file
     */
    fun addSDKToBuildGradle(basePath: String, project: Project) {
        val projectBuildGradle = File("$basePath/build.gradle")
        var content: String = projectBuildGradle.readText()

        content = content.appendStringByMatch("mavenCentral\\(\\)", "gradlePluginPortal()", "\n\t\t", project)
        content = content.appendStringByMatch("jcenter\\(\\)", "gradlePluginPortal()", "\n\t\t", project)

        content = content.appendStringByMatch(
            "classpath 'com.android.tools.build:gradle:.+'",
            "gradle.plugin.com.onesignal:onesignal-gradle-plugin",
            "classpath 'gradle.plugin.com.onesignal:onesignal-gradle-plugin:[0.12.9, 0.99.99]'", "\n\t\t", project
        )
        content = content.appendStringByMatch(
            "classpath\\s\"com\\.android\\.tools\\.build:gradle:.+\"",
            "gradle.plugin.com.onesignal:onesignal-gradle-plugin",
            "classpath \"gradle.plugin.com.onesignal:onesignal-gradle-plugin:[0.12.9, 0.99.99]\"", "\n\t\t", project
        )

        projectBuildGradle.writeText(content)
    }
}