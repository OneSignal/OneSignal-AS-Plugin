package sdksetup

import OneSignalStep
import OneSignalStepListener
import com.intellij.openapi.project.Project
import utils.showNotification
import view.MultilineLabel
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.JButton
import javax.swing.JPanel
import javax.swing.JTextField

class SDKSetupSecondStepPanel(
    private val basePath: String,
    private val project: Project,
    private val stepListener: OneSignalStepListener
) : JPanel(),
    OneSignalStep {

    private val controller = SDKSetupSecondStepController()
    private val instructionString = """
       OneSignal SDK needs the following changes in your application build.gradle
       
       apply plugin: 'com.onesignal.androidsdk.onesignal-gradle-plugin'

       dependencies {
            implementation('com.onesignal:OneSignal:4.6.3')
       }
       
       Be sure that before next button is clicked your Gradle is sync.
       
       Note: the changes will be made inside your base project path 
       -> BASE_PROJECT_PATH/app/build.gradle
       If your application build.gradle is not in that path then add the correct path into the Field.
    """
    private var instructionsLabel: MultilineLabel = MultilineLabel(instructionString)
    private var nextButton: JButton = JButton("Next")
    private var cancelButton: JButton = JButton("Cancel")
    private var appDirectoryField: JTextField = JTextField(30)

    init {

        initScreenPanel()
    }

    private fun initScreenPanel() {
        layout = GridBagLayout()

        var bagConstraints = GridBagConstraints()

        // Instructions Label

        bagConstraints.gridx = 0
        bagConstraints.gridy = 0
        bagConstraints.fill = GridBagConstraints.HORIZONTAL
        bagConstraints.weightx = 1.0
        bagConstraints.weighty = 1.0

        add(instructionsLabel, bagConstraints)

        // Text Field

        bagConstraints.fill = GridBagConstraints.CENTER
        bagConstraints.gridy = 1
        bagConstraints.gridx = 0
        bagConstraints.weightx = 1.0
        bagConstraints.weighty = 0.1

        add(appDirectoryField, bagConstraints)

        // Next Button

        bagConstraints.fill = GridBagConstraints.CENTER
        bagConstraints.gridy = 2
        bagConstraints.gridx = 1
        bagConstraints.weightx = 1.0
        bagConstraints.weighty = 0.1

        add(nextButton, bagConstraints)

        // Cancel Button

        bagConstraints.gridy = 2
        bagConstraints.gridx = 0
        bagConstraints.weightx = 1.0
        bagConstraints.weighty = 0.1

        add(cancelButton, bagConstraints)

        initListeners()
    }

    private fun initListeners() {
        nextButton.addActionListener {
            val buildGradlePath = appDirectoryField.text
            showNotification(project, "appDirectory $buildGradlePath")
            if (buildGradlePath.isEmpty())
                controller.addSDKToAppBuildGradle(basePath, "app", project = project)
            else
                controller.addSDKToAppBuildGradle(buildGradlePath, project = project)
            stepListener.onNextStep()
        }
        cancelButton.addActionListener {
            stepListener.onStepCancel()
        }
    }

    override fun getContent() = this
}