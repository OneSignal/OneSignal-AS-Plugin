package sdksetup

import utils.appendStringByMatch
import java.io.File

class SDKSetupFirstStepController {
    /**
     * Add dependencies to project level build.gradle file
     */
    fun addSDKToBuildGradle(basePath: String) {
        val projectBuildGradle = File("$basePath/build.gradle")
        var content: String = projectBuildGradle.readText()

        content = content.appendStringByMatch("mavenCentral\\(\\)", "gradlePluginPortal()", "\n\t\t")
        content = content.appendStringByMatch("jcenter\\(\\)", "gradlePluginPortal()", "\n\t\t")

        content = content.appendStringByMatch(
            "classpath 'com.android.tools.build:gradle:.+'",
            "classpath 'gradle.plugin.com.onesignal:onesignal-gradle-plugin:[0.12.9, 0.99.99]'", "\n\t\t"
        )
        content = content.appendStringByMatch(
            "classpath\\s\"com\\.android\\.tools\\.build:gradle:.+\"",
            "classpath \"gradle.plugin.com.onesignal:onesignal-gradle-plugin:[0.12.9, 0.99.99]\"", "\n\t\t"
        )

        projectBuildGradle.writeText(content)
    }
}