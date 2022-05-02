import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JPanel

class WelcomeScreenPanel(private val stepListener: OneSignalStepListener) : JPanel(), OneSignalStep {

    var welcomeLabel: JLabel
    var nextButton: JButton

    init {
        welcomeLabel = JLabel("Welcome to OneSignal Plugin")
        nextButton = JButton("Next")

        initScreenPanel()
    }

    private fun initScreenPanel() {
        layout = GridBagLayout()

        var bagConstraints = GridBagConstraints()

        // Welcome Label

        bagConstraints.gridx = 1
        bagConstraints.gridy = 0
        bagConstraints.weightx = 1.0
        bagConstraints.weighty = 1.0

        add(welcomeLabel, bagConstraints)

        // Next Button

        bagConstraints.gridx = 1
        bagConstraints.gridy = 1
        bagConstraints.weightx = 1.0
        bagConstraints.weighty = 0.1
        add(nextButton, bagConstraints)

        initListeners()
    }

    private fun initListeners() {
        nextButton.addActionListener {
            stepListener.onNextStep()
        }
    }

    override fun getContent() = this
}
