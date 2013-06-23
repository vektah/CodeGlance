package net.vektah.codeglance;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * Created with IntelliJ IDEA.
 * User: adam
 * Date: 1/15/13
 * Time: 11:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class CodeGlancePlugin implements ProjectComponent {
    private Project project;
    private Logger logger = Logger.getInstance(getClass());

    public CodeGlancePlugin(Project project) {
        this.project = project;
        logger.warn("Constructed");
    }



    public void initComponent() {
	    project.getMessageBus().connect().subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, new EditorPanelInjector(project));
        logger.warn("init");
    }

    public void disposeComponent() {
        // TODO: insert component disposal logic here
    }

    @NotNull
    public String getComponentName() {
        return "CodeGlancePlugin";
    }

    public void projectOpened() {
        // called when project is opened
    }

    public void projectClosed() {
        // called when project is being closed
    }
}
