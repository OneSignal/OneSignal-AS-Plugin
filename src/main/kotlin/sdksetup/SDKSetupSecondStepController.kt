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
            "com.onesignal:OneSignal",
            "implementation \"com.onesignal:OneSignal:[4.0.0, 4.99.99]\"",
            "\n\t"
        )
        content = content.appendStringByMatch(
            "implementation \"com.google.android.material:material:[^']*\"",
            "com.onesignal:OneSignal",
            "implementation \"com.onesignal:OneSignal:[4.0.0, 4.99.99]\"",
            "\n\t"
        )

        content = content.appendStringByMatch(
            "implementation 'androidx.appcompat:appcompat:[^']*'",
            "com.onesignal:OneSignal",
            "implementation 'com.onesignal:OneSignal:[4.0.0, 4.99.99]'",
            "\n\t"
        )
        content = content.appendStringByMatch(
            "implementation 'com.google.android.material:material:[^']*'",
            "com.onesignal:OneSignal",
            "implementation 'com.onesignal:OneSignal:[4.0.0, 4.99.99]'",
            "\n\t"
        )

        // injecting onesignal-gradle-plugin

        content = content.appendStringByMatch(
            "apply plugin: 'com.android.application'",
            "com.onesignal.androidsdk.onesignal-gradle-plugin",
            "apply plugin: 'com.onesignal.androidsdk.onesignal-gradle-plugin'",
            "\n"
        )
        content = content.appendStringByMatch(
            "id 'com.android.application'",
            "com.onesignal.androidsdk.onesignal-gradle-plugin",
            "id 'com.onesignal.androidsdk.onesignal-gradle-plugin'",
            "\n\t"
        )

        projectBuildGradle.writeText(content)
    }
}