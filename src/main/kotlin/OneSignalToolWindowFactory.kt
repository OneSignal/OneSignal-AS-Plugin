import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory

import java.awt.CardLayout
import javax.swing.JPanel

class OneSignalToolWindowFactory : ToolWindowFactory {

    private var project: Project? = null
    private var toolWindow: ToolWindow? = null
    private var mainPanel: JPanel? = null
    private var mainCardLayout = CardLayout()

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

        val welcomePanel = WelcomeScreenPanel()

        this.mainPanel = JPanel(mainCardLayout).apply {
            add(welcomePanel, welcomeKey)
        }

        val contentFactory = ContentFactory.SERVICE.getInstance()
        val content = contentFactory.createContent(mainPanel, "", false)
        toolWindow.contentManager.addContent(content)
    }
}
