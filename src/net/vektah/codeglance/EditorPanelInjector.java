package net.vektah.codeglance;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorManagerEvent;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

import javax.swing.*;
import java.awt.*;

/**
 * Injects a panel into any newly created editors.
 */
public class EditorPanelInjector implements FileEditorManagerListener {
    private Project project;
    private Logger logger = Logger.getInstance(getClass());

    public EditorPanelInjector(Project project) {
        this.project = project;
    }

    @Override
    public void fileOpened(FileEditorManager fileEditorManager, VirtualFile virtualFile) {
        JComponent component = fileEditorManager.getSelectedEditor(virtualFile).getComponent();
        if(component instanceof JPanel) {
            JPanel impl = (JPanel)component;
            if(impl.getLayout() instanceof BorderLayout) {
                GlancePanel panel = new GlancePanel(project, fileEditorManager.getSelectedEditor(virtualFile));
                impl.add(panel, BorderLayout.LINE_END);;
                logger.warn("Injected a new editor panel!");
            } else {
                logger.error("Not a BorderLayout:" .concat (impl.getLayout().getClass().getName()));
            }
        } else {
            logger.error("Not a jpanel");
        }
    }

    @Override
    public void fileClosed(FileEditorManager fileEditorManager, VirtualFile virtualFile) { }

    @Override
    public void selectionChanged(FileEditorManagerEvent fileEditorManagerEvent) { }
}
