import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory

import sdksetup.SDKSetupFirstStepPanel
import sdksetup.SDKSetupSecondStepPanel

import java.awt.CardLayout
import javax.swing.JComponent
import javax.swing.JPanel

class OneSignalToolWindowFactory : ToolWindowFactory, OneSignalStepListener {

    private var project: Project? = null
    private var toolWindow: ToolWindow? = null
    private var mainPanel: JPanel? = null
    private var mainCardLayout = CardLayout()

    private var sdkSetupSteps = linkedMapOf<String, OneSignalStep>()
    private var sdkSetupStepIndex = 0
    private var welcomeKey = "welcome_panel"

    /**
     * Create the tool window content.
     *
     * @param project    current project
     * @param toolWindow current tool window
     */
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        this.project = project
        this.toolWindow = toolWindow

        // If basePath is null add step to get basePath
        sdkSetupSteps["first_step_panel"] = SDKSetupFirstStepPanel(project.basePath!!, this@OneSignalToolWindowFactory)
        sdkSetupSteps["second_step_panel"] = SDKSetupSecondStepPanel(project.basePath!!, this@OneSignalToolWindowFactory)

        val welcomePanel = WelcomeScreenPanel(this@OneSignalToolWindowFactory)

        this.mainPanel = JPanel(mainCardLayout).apply {
            add(welcomePanel, welcomeKey)

            sdkSetupSteps.entries.forEach {
                add(it.value as JComponent, it.key)
            }
        }

        val contentFactory = ContentFactory.SERVICE.getInstance()
        val content = contentFactory.createContent(mainPanel, "", false)
        toolWindow.contentManager.addContent(content)
    }

    override fun onNextStep() {
        var index = 0
        val keysIterator = sdkSetupSteps.keys.iterator()
        while (keysIterator.hasNext()) {
            val key = keysIterator.next()
            if (index == sdkSetupStepIndex) {
                sdkSetupStepIndex++
                showPanel(key)
                break
            }
            index++
        }
    }

    private fun showPanel(panelName: String) {
        mainCardLayout.show(mainPanel, panelName);
    }
}
