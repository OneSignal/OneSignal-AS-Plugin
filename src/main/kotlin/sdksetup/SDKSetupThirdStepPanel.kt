package sdksetup

import OneSignalStep
import OneSignalStepListener
import com.intellij.openapi.project.Project
import exception.OneSignalException
import utils.showNotification
import view.MultilineLabel
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.JButton
import javax.swing.JPanel
import javax.swing.JTextField

class SDKSetupThirdStepPanel(
    private val basePath: String,
    private val project: Project,
    private val stepListener: OneSignalStepListener
) : JPanel(),
    OneSignalStep {

    private val controller = SDKSetupThirdStepController()
    private val instructionString = """
       The OneSignal SDK needs an Application class in order to add the init code.
       If you already have one, the OneSignal Plugin will search inside your Manifest for it.
       If you already have one, and the OneSignal Plugin cannot find it, you may need to manually copy the initialization code below into your class.
       
       If the project doesn't have an Application Class, the OneSignal Plugin will create one for you.
       The Application class will be created inside the main project package.
       
       If you already have a OneSignal app ID, then copy and paste it inside the field.
       If not, you can continue without an app ID, and add it later.
        
       Init code:
       OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE)
       OneSignal.initWithContext(this)
       OneSignal.setAppId(ONESIGNAL_APP_ID)
       
       Note: Reload from Disk might be needed when creating the Application class.
    """
    private var instructionsLabel: MultilineLabel = MultilineLabel(instructionString)
    private var nextButton: JButton = JButton("Next")
    private var cancelButton: JButton = JButton("Cancel")
    private var appIdField: JTextField = JTextField(30)

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

        add(appIdField, bagConstraints)

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

            showNotification(project, "Third step appDirectory: ${stepListener.getAppDirectory()}")

            var appId = appIdField.text
            if (appId.isEmpty())
                appId = "YOUR_ONESIGNAL_APP_ID"

            try {
                controller.applicationOneSignalCodeInjection(
                    stepListener.getAppBuildGradlePath() ?: basePath,
                    stepListener.getAppDirectory(),
                    appId,
                    project
                )

                stepListener.onNextStep()
            } catch (exception: OneSignalException) {
                exception.message?.let {
                    showNotification(project, exception.message)
                }
            }
        }
        cancelButton.addActionListener {
            stepListener.onStepCancel()
        }
    }

    override fun getContent() = this
}