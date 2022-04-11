import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.JLabel
import javax.swing.JPanel

class WelcomeScreenPanel : JPanel() {

    var welcomeLabel: JLabel

    init {
        welcomeLabel = JLabel("Welcome to the OneSignal Plugin")

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
    }
}
