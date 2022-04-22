package sdksetup

import com.intellij.openapi.project.Project
import exception.OneSignalException
import exception.manifestPathNotFound
import exception.nonTraceableError
import exception.packageNotFound
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

    fun applicationOneSignalCodeInjection(basePath: String, appDirectory: String, appId: String, project: Project) {
        val packagePathSequence = getApplicationPackage(basePath, appDirectory, project)
            ?: throw OneSignalException(packageNotFound)

        val projectDirectory = buildPath(packagePathSequence) // "com/onesignal/sdktest"
        val applicationPath =
            getApplicationFilePath(basePath, appDirectory, project) // null or ".application.MainApplication"

        if (applicationPath != null) {
            val applicationFilePath =
                "$projectDirectory${buildPath(applicationPath)}" // "com/onesignal/sdktest/application/MainApplication"
            showNotification(project, "Application applicationFilePath: $applicationFilePath")

            val resultPath: String
            val javaPath = "$basePath/$appDirectory/src/main/java/$applicationFilePath.java"
            val kotlinPath = "$basePath/$appDirectory/src/main/kotlin/$applicationFilePath.kt"

            resultPath = when {
                Files.exists(Paths.get(javaPath)) -> {
                    javaPath
                }
                Files.exists(Paths.get(kotlinPath)) -> {
                    kotlinPath
                }
                else -> {
                    // Track error
                    throw OneSignalException(nonTraceableError)
                }
            }
            showNotification(project, "resultPath: $resultPath")
            addInitCodeToFile(resultPath, appId, project)
        } else {
            // Improvement Note: Add an option for the user to choose between Kotlin and Java for the Application class generated
            val applicationClassName = "MainApplication"
            val newApplicationFilePath =
                "$basePath/$appDirectory/src/main/java/$projectDirectory/$applicationClassName.java"
            createApplicationFile(newApplicationFilePath, packagePathSequence, project)
            addInitCodeToFile(newApplicationFilePath, appId, project)

            // We need to modify Manifest file and add application name to the created path
            // android:name=".application.MainApplication"

            addApplicationToManifest(basePath, appDirectory,".$applicationClassName", project)
        }
    }

    /**
     * Method that search for the AndroidManifest.xml file inside default package $basePath/app/src/main/AndroidManifest.xml
     *
     * @return manifest file
     */
    private fun getManifestFile(basePath: String, appDirectory: String): File {
        val manifestFilePath = "$basePath/$appDirectory/src/main/AndroidManifest.xml"

        val manifestFileExist = Files.exists(Paths.get(manifestFilePath))

        if (!manifestFileExist) {
            throw OneSignalException(manifestPathNotFound)
        }

        // Check if this file exist, if not print error ask user for correct path
        return File(manifestFilePath)
    }

    /**
     * Method that search inside AndroidManifest.xml file for the project package
     *
     * @return Ex."com.onesignal.sdktest"
     */
    private fun getApplicationPackage(basePath: String, appDirectory: String, project: Project): String? {
        val content = getManifestFile(basePath, appDirectory).readText()

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

        return null
    }

    /**
     * Method that checks inside the AndroidManifest.xml if there exists an Application File configured
     * under the <application > tag
     *
     * @return Ex.".application.MainApplication"
     */
    private fun getApplicationFilePath(basePath: String, appDirectory: String, project: Project): String? {
        val content = getManifestFile(basePath, appDirectory).readText()
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

    private fun addApplicationToManifest(
        basePath: String,
        appDirectory: String,
        applicationPackage: String,
        project: Project
    ) {
        val manifestFile = getManifestFile(basePath, appDirectory)

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

    /**
     * Inject OneSignal init code to Application file
     *
     * Precondition: file exist at given path
     */
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
            "OneSignal.setLogLevel",
            """
        // Enable verbose OneSignal logging to debug issues if needed.
        // It is recommended you remove this after validating your implementation.
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);""",
            "\n\t",
            project
        )

        result = result.appendStringByMatch(
            "OneSignal.setLogLevel(.*)",
            "OneSignal.setAppId",
            """
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
            "OneSignal.setLogLevel",
            """
        // Enable verbose OneSignal logging to debug issues if needed.
        // It is recommended you remove this after validating your implementation.
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE)""",
            "\n\t",
            project
        )

        result = result.appendStringByMatch(
            "OneSignal.setLogLevel(.*)",
            "OneSignal.setAppId",
            """
        // OneSignal Initialization
        OneSignal.initWithContext(this)
        OneSignal.setAppId(oneSignalAppId)""",
            "\n\t",
            project
        )

        return result
    }

}