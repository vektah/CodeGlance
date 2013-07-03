/*
 * Copyright © 2013, Adam Scarr
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package net.vektah.codeglance;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileEditor.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import net.vektah.codeglance.render.TaskRunner;

import javax.swing.*;
import java.awt.*;

/**
 * Injects a panel into any newly created editors.
 */
public class EditorPanelInjector implements FileEditorManagerListener {
	private Project project;
	private Logger logger = Logger.getInstance(getClass());
	private TaskRunner runner;

	public EditorPanelInjector(Project project, TaskRunner runner) {
		this.project = project;
		this.runner = runner;
	}

	@Override
	public void fileOpened(FileEditorManager fileEditorManager, VirtualFile virtualFile) {

		// Seems there is a case where multiple split panes can have the same file open and getSelectedEditor, and even
		// getEditors(virtualVile) return only one of them... So shotgun approach here.
		FileEditor[] editors = fileEditorManager.getAllEditors();
		for(FileEditor editor: editors) {
			inject(editor);
		}
	}

	/**
	 * Here be dragons. No Seriously. Run!
	 *
	 * There is a loading pane that proxies stuff here blah blah.. We need to dig down so we can check
	 * if we have already injected into a given component... On the plus side might be a bit closer to being able to
	 * injecting into the editor space itself...
	 *
	 * @param editor A text editor to inject into.
	 */
	private void inject(FileEditor editor) {
		if(!(editor instanceof TextEditor)) {
			logger.info("I01: Injection failed, only text editors are supported currently.");
			return;
		}

		if(!(editor.getComponent() instanceof JPanel)) {
			logger.warn("I02: Injection failed, Not a JPanel");
			return;
		}
		JPanel outerPanel = (JPanel)editor.getComponent();

		if(!(outerPanel.getLayout() instanceof BorderLayout)) {
			logger.warn("I03: Injection failed, could not find a outer BorderLayout");
			return;
		}
		BorderLayout outerLayout = (BorderLayout)outerPanel.getLayout();

		if(!(outerLayout.getLayoutComponent(BorderLayout.CENTER) instanceof JLayeredPane)) {
			logger.warn("I04: Injection failed, could not find a layered pane (loading screen?)");
			return;
		}
		JLayeredPane pane = (JLayeredPane)outerLayout.getLayoutComponent(BorderLayout.CENTER);

		if(!(pane.getComponent(1) instanceof JPanel)) {
			logger.warn("I05: Injection failed, could not find a pane");
			return;
		}
		JPanel panel = (JPanel)pane.getComponent(1);

		if(!(panel.getLayout() instanceof BorderLayout)) {
			logger.warn("I06: Injection failed, could not find a border layout");
			return;
		}
		BorderLayout innerLayout = (BorderLayout)panel.getLayout();

		// Ok we finally found the actual editor layout. Now make sure we haven't already injected into this editor.
		if(innerLayout.getLayoutComponent(BorderLayout.LINE_END) == null) {
			GlancePanel glancePanel = new GlancePanel(project, editor, panel, runner);
			panel.add(glancePanel, BorderLayout.LINE_END);;
		} else {
			logger.info("I07: Injection skipped. Looks like we have already injected something here.");
		}
	}

	@Override
	public void fileClosed(FileEditorManager fileEditorManager, VirtualFile virtualFile) { }

	@Override
	public void selectionChanged(FileEditorManagerEvent fileEditorManagerEvent) { }
}
