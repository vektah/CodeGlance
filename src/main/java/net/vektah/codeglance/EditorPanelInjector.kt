package net.vektah.codeglance

import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.fileEditor.*
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.JBSplitter
import net.vektah.codeglance.config.Config
import net.vektah.codeglance.config.ConfigService
import net.vektah.codeglance.render.TaskRunner
import javax.swing.*
import java.awt.*
import java.util.*

/**
 * Injects a panel into any newly created editors.
 */
class EditorPanelInjector(private val project: Project, private val runner: TaskRunner) : FileEditorManagerListener {
    private val logger = Logger.getInstance(javaClass)
    private val panels = HashMap<FileEditor, GlancePanel>()
    private val configService = ServiceManager.getService(ConfigService::class.java)
    private var config: Config = configService.state!!

    override fun fileOpened(fem: FileEditorManager, virtualFile: VirtualFile) {
        // Seems there is a case where multiple split panes can have the same file open and getSelectedEditor, and even
        // getEditors(virtualVile) return only one of them... So shotgun approach here.
        val editors = fem.allEditors
        for (editor in editors) {
            inject(editor)
        }

        freeUnusedPanels(fem)
    }

    /**
     * Here be dragons. No Seriously. Run!
     *
     * We are digging way down into the editor layout. This lets the codeglance panel be right next to the scroll bar.
     * In an ideal world it would be inside the scroll bar... maybe one day.
     *
     * vsch: added handling when the editor is even deeper, inside firstComponent of a JBSplitter, used by idea-multimarkdown
     * and Markdown Support to show split preview. Missed this plugin while editing markdown. These changes got it back.
     *
     * @param editor A text editor to inject into.
     */
    private fun getPanel(editor: FileEditor): JPanel? {
        if (editor !is TextEditor) {
            logger.debug("I01: Injection failed, only text editors are supported currently.")
            return null
        }

        try {
            val outerPanel = editor.component as JPanel
            val outerLayout = outerPanel.layout as BorderLayout
            var layoutComponent = outerLayout.getLayoutComponent(BorderLayout.CENTER)

            if (layoutComponent is JBSplitter) {
                // editor is inside firstComponent of a JBSplitter
                val editorComp = layoutComponent.firstComponent as JPanel
                layoutComponent = (editorComp.layout as BorderLayout).getLayoutComponent(BorderLayout.CENTER)
            }

            val pane = layoutComponent as JLayeredPane
            val panel = if (pane.componentCount > 1) pane.getComponent(1) as JPanel else pane.getComponent(0) as JPanel

            // Assert ahead of time that we have the expected layout, so the caller dosent need to
            panel.layout as BorderLayout

            return panel
        } catch (e: ClassCastException) {
            logger.warn("Injection failed")
            e.printStackTrace()
            return null
        }
    }

    private fun inject(editor: FileEditor) {
        val panel = getPanel(editor) ?: return
        val innerLayout = panel.layout as BorderLayout

        val where = if (config.isRightAligned) BorderLayout.LINE_END else BorderLayout.LINE_START

        if (innerLayout.getLayoutComponent(where) == null) {
            val glancePanel = GlancePanel(project, editor, panel, runner)
            panel.add(glancePanel, where)
            panels.put(editor, glancePanel)
        }
    }

    private fun uninject(editor: FileEditor) {
        val panel = getPanel(editor) ?: return
        val innerLayout = panel.layout as BorderLayout

        // Ok we finally found the actual editor layout. Now make sure we have already injected into this editor.
        val rightPanel = innerLayout.getLayoutComponent(BorderLayout.LINE_END)
        if (rightPanel != null) {
            panel.remove(rightPanel)
        }

        val leftPanel = innerLayout.getLayoutComponent(BorderLayout.LINE_START)
        if (leftPanel != null) {
            panel.remove(leftPanel)
        }
    }

    override fun fileClosed(fem: FileEditorManager, virtualFile: VirtualFile) {
        freeUnusedPanels(fem)
    }

    /**
     * On close we dont know which (if any) editor was closed, just the file. And in configurations we dont even
     * get a fileClosed event. Lets just scan all of the open penels and make sure they are still being used by
     * at least one of the open editors.
     */
    private fun freeUnusedPanels(fem: FileEditorManager) {
        val unseen = HashSet(panels.keys)

        for (editor in fem.allEditors) {
            if (unseen.contains(editor)) {
                unseen.remove(editor)
            }
        }

        var panel: GlancePanel
        for (editor in unseen) {
            panel = panels[editor]!!
            panel.onClose()
            uninject(editor)
            panels.remove(editor)
        }
    }

    override fun selectionChanged(fileEditorManagerEvent: FileEditorManagerEvent) {
    }
}
