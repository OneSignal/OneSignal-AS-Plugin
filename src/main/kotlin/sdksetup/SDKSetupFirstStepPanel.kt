package sdksetup

import OneSignalStep
import OneSignalStepListener
import view.MultilineLabel
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.JButton
import javax.swing.JPanel

class SDKSetupFirstStepPanel(
    private val basePath: String,
    private val stepListener: OneSignalStepListener
) : JPanel(),
    OneSignalStep {

    private val controller = SDKSetupFirstStepController()
    private val instructionString = """
        OneSignal SDK needs the following changes in your build.gradle file

        buildscript {

            repositories {
                ...
                gradlePluginPortal()
            }

            dependencies {

               classpath 'com.onesignal:onesignal-gradle-plugin:[0.8.1, 0.99.99]'
            }
       }
       
       Be sure that before next button is clicked your Gradle is sync
    """
    private var instructionsLabel: MultilineLabel = MultilineLabel(instructionString)
    private var nextButton: JButton = JButton("Next")
    private var cancelButton: JButton = JButton("Cancel")

    init {

        initScreenPanel()
    }

    private fun initScreenPanel() {
        layout = GridBagLayout()

        var bagConstraints = GridBagConstraints()

        // Instructions Label

        bagConstraints.gridy = 0
        bagConstraints.fill = GridBagConstraints.HORIZONTAL
        bagConstraints.weightx = 1.0
        bagConstraints.weighty = 1.0

        add(instructionsLabel, bagConstraints)

        // Next Button

        bagConstraints.gridy = 1
        bagConstraints.gridx = 1
        bagConstraints.weightx = 0.2
        bagConstraints.weighty = 0.1

        add(nextButton, bagConstraints)

        // Cancel Button

        bagConstraints.gridy = 1
        bagConstraints.gridx = 0
        bagConstraints.weightx = 0.2
        bagConstraints.weighty = 0.1

        add(cancelButton, bagConstraints)

        initListeners()
    }

    private fun initListeners() {
        nextButton.addActionListener {
            controller.addSDKToBuildGradle(basePath)
            stepListener.onNextStep()
        }
        cancelButton.addActionListener {
            stepListener.onStepCancel()
        }
    }

    override fun getContent() = this
}