package sdksetup

import com.intellij.openapi.project.Project
import utils.appendStringByMatch
import java.io.File

class SDKSetupSecondStepController {

    fun addSDKToAppBuildGradle(basePath: String, appDirectory: String, project: Project) {
        addSDKToAppBuildGradle("$basePath/$appDirectory", project)
    }

    fun addSDKToAppBuildGradle(buildGradlePath: String, project: Project) {
        val projectBuildGradle = File("$buildGradlePath/build.gradle")
        var content: String = projectBuildGradle.readText()

        // injecting com.onesignal:OneSignal dependency

        content = content.appendStringByMatch(
            "implementation \"androidx.appcompat:appcompat:[^']*\"",
            "com.onesignal:OneSignal",
            "implementation \"com.onesignal:OneSignal:[4.0.0, 4.99.99]\"",
            "\n\t",
            project
        )
        content = content.appendStringByMatch(
            "implementation \"com.google.android.material:material:[^']*\"",
            "com.onesignal:OneSignal",
            "implementation \"com.onesignal:OneSignal:[4.0.0, 4.99.99]\"",
            "\n\t",
            project
        )

        content = content.appendStringByMatch(
            "implementation 'androidx.appcompat:appcompat:[^']*'",
            "com.onesignal:OneSignal",
            "implementation 'com.onesignal:OneSignal:[4.0.0, 4.99.99]'",
            "\n\t",
            project
        )
        content = content.appendStringByMatch(
            "implementation 'com.google.android.material:material:[^']*'",
            "com.onesignal:OneSignal",
            "implementation 'com.onesignal:OneSignal:[4.0.0, 4.99.99]'",
            "\n\t",
            project
        )

        // injecting onesignal-gradle-plugin

        content = content.appendStringByMatch(
            "apply plugin: 'com.android.application'",
            "com.onesignal.androidsdk.onesignal-gradle-plugin",
            "apply plugin: 'com.onesignal.androidsdk.onesignal-gradle-plugin'",
            "\n",
            project
        )
        content = content.appendStringByMatch(
            "id 'com.android.application'",
            "com.onesignal.androidsdk.onesignal-gradle-plugin",
            "id 'com.onesignal.androidsdk.onesignal-gradle-plugin'",
            "\n\t",
            project
        )

        projectBuildGradle.writeText(content)
    }
}