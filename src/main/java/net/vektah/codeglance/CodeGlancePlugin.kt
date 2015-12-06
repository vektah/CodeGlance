package net.vektah.codeglance

import com.intellij.openapi.components.ProjectComponent
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.project.Project
import net.vektah.codeglance.config.ConfigService
import net.vektah.codeglance.render.TaskRunner

class CodeGlancePlugin(private val project: Project) : ProjectComponent {
    private val logger = Logger.getInstance(javaClass)
    private val runner = TaskRunner()
    private val runnerThread = Thread(runner)
    private val injector: EditorPanelInjector

    init {
        injector = EditorPanelInjector(project, runner)
    }

    override fun initComponent() {
        ServiceManager.getService(ConfigService::class.java).state!!.disabled = false
        runnerThread.start()
        project.messageBus.connect().subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, injector)
        logger.debug("CodeGlance2 initialized")
    }

    override fun disposeComponent() {
        runner.stop()
    }

    override fun getComponentName(): String {
        return "CodeGlancePlugin"
    }

    override fun projectOpened() {
        // called when project is opened
    }

    override fun projectClosed() {
        // called when project is being closed
    }
}
