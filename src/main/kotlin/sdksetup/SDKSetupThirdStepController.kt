package sdksetup

import com.intellij.openapi.project.Project
import utils.appendStringByMatch
import utils.showNotification
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

class SDKSetupThirdStepController {

    enum class FileType(val extension: String) {
        JAVA("java"),
        KOTLIN("kt"),
        ;
    }

    private fun getFileType(value: String) =
        if (FileType.JAVA.extension == value)
            FileType.JAVA
        else
            FileType.KOTLIN

    fun applicationOneSignalCodeInjection(basePath: String, appId: String, project: Project) {
        val packagePathSequence = getApplicationPackage(basePath, project)

        if (packagePathSequence == null) {
            // Display error, ask user for Application path
            showNotification(project, "Application not packagePathSequence found: $packagePathSequence")
            return
        }

        val projectDirectory = buildPath(packagePathSequence) // "com/onesignal/sdktest"
        val applicationPath = getApplicationFilePath(basePath, project) // null or ".application.MainApplication"

        showNotification(project, "Application projectDirectory: $projectDirectory")
        showNotification(project, "Application applicationPath: $applicationPath")
        if (applicationPath != null) {
            val applicationFilePath =
                "$projectDirectory${buildPath(applicationPath)}" // "com/onesignal/sdktest/application/MainApplication"
            showNotification(project, "Application applicationFilePath: $applicationFilePath")

            var resultPath = ""
            val javaPath = "$basePath/app/src/main/java/$applicationFilePath.java"
            val kotlinPath = "$basePath/app/src/main/kotlin/$applicationFilePath.kt"

            when {
                Files.exists(Paths.get(javaPath)) -> {
                    resultPath = javaPath
                }
                Files.exists(Paths.get(kotlinPath)) -> {
                    resultPath = kotlinPath
                }
                else -> {
                    // Error no application class found
                }
            }
            showNotification(project, "resultPath: $resultPath")
            addInitCodeToFile(resultPath, "app_id_test", project)
        } else {
            // Improvement Note: Add an option for the user to choose between Kotlin and Java for the Application class generated
            val applicationClassName = "MainApplication"
            val newApplicationFilePath =
                "$basePath/app/src/main/java/$projectDirectory/$applicationClassName.java"
            createApplicationFile(newApplicationFilePath, packagePathSequence, project)
            addInitCodeToFile(newApplicationFilePath, appId, project)

            // We need to modify Manifest file and add application name to the created path
            // android:name=".application.MainApplication"

            addApplicationToManifest(basePath, ".$applicationClassName", project)
        }
    }

    private fun getManifestFile(basePath: String, project: Project): File {
        val manifestFilePath = "$basePath/app/src/main/AndroidManifest.xml"
        showNotification(project, "manifestFilePath: $manifestFilePath")
        // Check if this file exist, if not print error ask user for correct path
        return File(manifestFilePath)
    }

    /**
     * Method that search for the AndroidManifest.xml file inside default package $basePath/app/src/main/AndroidManifest.xml
     *
     * @return manifest content
     */
    private fun getManifestContent(basePath: String, project: Project): String {
        return getManifestFile(basePath, project).readText()
    }

    /**
     * Method that search inside AndroidManifest.xml file for the project package
     *
     * @return Ex."com.onesignal.sdktest"
     */
    private fun getApplicationPackage(basePath: String, project: Project): String? {
        val content = getManifestContent(basePath, project)

        val packageRegex = "package=\".+\"".toRegex()
        val packageMatch = packageRegex.find(content)?.range

        if (packageMatch != null) {
            val packageString = content.subSequence(packageMatch.first, packageMatch.last + 1)

            showNotification(project, "Manifest packageString: $packageString")

            return "\".+\"".toRegex().find(packageString)?.range?.let {
                val applicationPackage =
                    packageString.subSequence(it.first + 1, it.last)

                showNotification(project, "Manifest applicationPackage: $applicationPackage")

                applicationPackage.toString()
            }
        }
        // Show error, no package found
        return null
    }

    /**
     * Method that checks inside the AndroidManifest.xml if there exists an Application File configured
     * under the <application > tag
     *
     * @return Ex.".application.MainApplication"
     */
    private fun getApplicationFilePath(basePath: String, project: Project): String? {
        val content = getManifestContent(basePath, project)
        val applicationRegex = "<application[\\s\\S]+>".toRegex()

        val applicationMatch = applicationRegex.find(content)?.range

        if (applicationMatch != null) {
            val firstApplicationString = content.subSequence(applicationMatch.first, applicationMatch.last)
            showNotification(project, "firstApplicationString: $firstApplicationString")
            val charIndex = firstApplicationString.indexOfFirst {
                it == ">".single()
            }

            showNotification(project, "charIndex: $charIndex")

            val applicationString = firstApplicationString.subSequence(0, charIndex + 1)
            showNotification(project, "applicationString: $applicationString")

            val applicationNameMatch = "android:name=\".+\"".toRegex().find(applicationString)?.range

            showNotification(project, "applicationNameMatch: $applicationNameMatch")

            if (applicationNameMatch != null) {
                val applicationSubsequence =
                    applicationString.subSequence(applicationNameMatch.first, applicationNameMatch.last + 1)

                showNotification(project, "applicationString: $applicationSubsequence")
                return "\".+\"".toRegex().find(applicationSubsequence)?.range?.let {
                    val applicationName =
                        applicationSubsequence.subSequence(it.first + 1, it.last)

                    showNotification(project, "application name: $applicationName")

                    applicationName.toString()
                }
            }
        }

        // There is no Application class available
        return null
    }

    private fun buildPath(path: String): String =
        path.replace(".", "/")

    private fun createApplicationFile(applicationFileBasePath: String, projectDirectory: String, project: Project) {
        showNotification(project, "applicationFileBasePath: $applicationFileBasePath")
        showNotification(project, "projectDirectory: $projectDirectory")

        val applicationFile = File("$applicationFileBasePath")
        val result = applicationFile.createNewFile()

        showNotification(project, "Application class created: $result")

        var content: String = applicationFile.readText()
        content += "package $projectDirectory;\n\n"
        content += "import android.app.Application;\n\n"
        content += "public class MainApplication extends Application {\n"
        content += "\t@Override\n"
        content += "\tpublic void onCreate() {\n"
        content += "\t\tsuper.onCreate();\n"
        content += "\t}\n"
        content += "}"
        applicationFile.writeText(content)
    }

    private fun addApplicationToManifest(basePath: String, applicationPackage: String, project: Project) {
        val manifestFile = getManifestFile(basePath, project)

        var content: String = manifestFile.readText()

        content = content.appendStringByMatch(
            "<application",
            "android:name=\"$applicationPackage\"",
            "\n\t\t\t\t",
            project
        )

        showNotification(project, "addApplicationToManifest applicationPackage: $applicationPackage")
        manifestFile.writeText(content)
    }

    private fun addInitCodeToFile(applicationFilePath: String, appId: String, project: Project) {
        val applicationFile = File(applicationFilePath)
        val content: String = applicationFile.readText()

        showNotification(project, "FileType: ${getFileType(applicationFile.extension)}")

        when (getFileType(applicationFile.extension)) {
            FileType.JAVA ->
                getJavaInitCode(content, appId, project)
            FileType.KOTLIN ->
                getKotlinInitCode(content, appId, project)
        }.apply {
            showNotification(project, "Application class final string: \n$this")
            applicationFile.writeText(this)
        }
    }

    private fun getJavaInitCode(content: String, appId: String, project: Project): String {
        var result = content.appendStringByMatch(
            "import [a-zA-Z.]+;",
            "import com.onesignal.OneSignal;",
            "\n",
            project
        )

        result = result.appendStringByMatch(
            "class[a-zA-Z\\s]+\\{",
            "private static final String ONESIGNAL_APP_ID = \"$appId\";",
            "\n\t",
            project
        )

        result = result.appendStringByMatch(
            "super.onCreate\\(\\);",
            """
        // Enable verbose OneSignal logging to debug issues if needed.
        // It is recommended you remove this after validating your implementation.
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);
        // OneSignal Initialization
        OneSignal.initWithContext(this);
        OneSignal.setAppId(ONESIGNAL_APP_ID);""",
            "\n\t",
            project
        )

        return result
    }

    private fun getKotlinInitCode(content: String, appId: String, project: Project): String {
        var result = content.appendStringByMatch(
            "import [a-zA-Z.]+",
            "import com.onesignal.OneSignal",
            "\n",
            project
        )

        result = result.appendStringByMatch(
            "class [a-zA-Z\\s:()]+\\{",
            "private val oneSignalAppId = \"$appId\"",
            "\n\t",
            project
        )

        result = result.appendStringByMatch(
            "super.onCreate\\(\\)",
            """// Enable verbose OneSignal logging to debug issues if needed.
        // It is recommended you remove this after validating your implementation.
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE)
        // OneSignal Initialization
        OneSignal.initWithContext(this)
        OneSignal.setAppId(oneSignalAppId)""",
            "\n\t",
            project
        )

        return result
    }

}