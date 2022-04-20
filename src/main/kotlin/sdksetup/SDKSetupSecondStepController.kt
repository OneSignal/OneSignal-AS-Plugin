package sdksetup

import utils.appendStringByMatch
import java.io.File

class SDKSetupSecondStepController {

    fun addSDKToAppBuildGradle(basePath: String, appDirectory: String) {
        addSDKToAppBuildGradle("$basePath/$appDirectory")
    }

    fun addSDKToAppBuildGradle(buildGradlePath: String) {
        val projectBuildGradle = File("$buildGradlePath/build.gradle")
        var content: String = projectBuildGradle.readText()

        // injecting com.onesignal:OneSignal dependency

        content = content.appendStringByMatch(
            "implementation \"androidx.appcompat:appcompat:[^']*\"",
            "implementation \"com.onesignal:OneSignal:[4.0.0, 4.99.99]\"",
            "\n\t\t"
        )
        content = content.appendStringByMatch(
            "implementation 'androidx.appcompat:appcompat:[^']*'",
            "implementation 'com.onesignal:OneSignal:[4.0.0, 4.99.99]'",
            "\n\t\t"
        )
        content = content.appendStringByMatch(
            "implementation \"com.google.android.material:material:[^']*\"",
            "implementation \"com.onesignal:OneSignal:[4.0.0, 4.99.99]\"",
            "\n\t\t"
        )
        content = content.appendStringByMatch(
            "implementation 'com.google.android.material:material:[^']*'",
            "implementation 'com.onesignal:OneSignal:[4.0.0, 4.99.99]'",
            "\n\t\t"
        )

        // injecting onesignal-gradle-plugin

        content = content.appendStringByMatch(
            "apply plugin: 'com.android.application'",
            "apply plugin: 'com.onesignal.androidsdk.onesignal-gradle-plugin'",
            "\n\t\t"
        )
        content = content.appendStringByMatch(
            "id 'com.android.application'",
            "id 'com.onesignal.androidsdk.onesignal-gradle-plugin'",
            "\n\t\t"
        )

        projectBuildGradle.writeText(content)
    }
}