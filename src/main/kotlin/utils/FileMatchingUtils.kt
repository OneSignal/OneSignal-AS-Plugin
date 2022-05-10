package utils

import com.android.tools.idea.gradle.dsl.parser.BuildModelContext
import com.android.tools.idea.gradle.dsl.parser.files.GradleBuildFile
import com.intellij.openapi.project.Project
/* Intelilj IDEA could not find GradleBuildModel @ com.android.tools.idea.gradle.dsl.model.GradleBuildModel */
import com.android.tools.idea.gradle.dsl.api.GradleBuildModel
import com.android.tools.idea.gradle.dsl.api.GradleModelProvider
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import java.io.File

/**
 * For testing, get the project 'build.gradle' file as a VirtualFile
 */
fun getProjectBuildGradleFile(project: Project) : VirtualFile {
    val basePath = project.basePath
    val projectBuildGradleFile = File("$basePath/build.gradle")
    val virtualFile = VfsUtil.findFileByIoFile(projectBuildGradleFile, true)

    return virtualFile!!
}

/**
 * This compiles but com.android.tools.idea.gradle.dsl.api.GradleModelProvider.get() not found at runtime
 * Testing making a GradleBuildModel to work off of
 */
fun makeGradleBuildModel(project: Project) {
    val projectBuildGradleFile = getProjectBuildGradleFile(project)
    showNotification(project, "makeGradleBuildModel() Project build.gradle found: $projectBuildGradleFile")

    // At runtime, throws java.lang.NoSuchMethodError:
    // 'com.android.tools.idea.gradle.dsl.api.GradleModelProvider com.android.tools.idea.gradle.dsl.api.GradleModelProvider.get()'
    try {
        val buildModel : GradleBuildModel = GradleModelProvider.get().parseBuildFile(projectBuildGradleFile, project)
        showNotification(project, "buildModel made: $buildModel")
    } catch (t: Throwable) {
        showNotification(project, "Throwable caught: $t")
    }
}

/**
 * This compiles but com/android/tools/idea/gradle/dsl/parser/BuildModelContext not found at runtime
 * Testing making a GradleBuildFile to work off of
 */
fun makeGradleBuildFile(project: Project) {
    var buildModelContext: BuildModelContext? = null

    val projectBuildGradleFile = getProjectBuildGradleFile(project)
    showNotification(project, "makeGradleBuildFile() Project build.gradle found: $projectBuildGradleFile")

    val projectName = project.name
    showNotification(project, "makeGradleBuildFile() projectName: $projectName")

    // At runtime, throws
    // java.lang.NoClassDefFoundError: com/android/tools/idea/gradle/dsl/parser/BuildModelContext

    try {
        buildModelContext = BuildModelContext.create(project)
        showNotification(project, "buildModelContext: $buildModelContext")

    }catch (t: Throwable) {
        showNotification(project, "Throwable caught $t")
    }

    // Because of java.lang.NoClassDefFoundError: com/android/tools/idea/gradle/dsl/parser/BuildModelContext
    // We cannot make the GradleBuildFile in the next step
    try {
        buildModelContext = BuildModelContext.create(project)
        val gradleBuildFile = GradleBuildFile(projectBuildGradleFile, project, project.name, buildModelContext)
        showNotification(project, "gradleBuildFile: $gradleBuildFile")

    }catch (t: Throwable) {
        showNotification(project, "Throwable caught $t")
    }
}
