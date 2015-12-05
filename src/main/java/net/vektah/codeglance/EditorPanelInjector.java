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
import java.util.*;

/**
 * Injects a panel into any newly created editors.
 */
public class EditorPanelInjector implements FileEditorManagerListener {
	private Project project;
	private Logger logger = Logger.getInstance(getClass());
	private TaskRunner runner;
	private Map<FileEditor, GlancePanel> panels = new HashMap<FileEditor, GlancePanel>();

	public EditorPanelInjector(Project project, TaskRunner runner) {
		this.project = project;
		this.runner = runner;
	}

	@Override
	public void fileOpened(FileEditorManager fem, VirtualFile virtualFile) {
		// Seems there is a case where multiple split panes can have the same file open and getSelectedEditor, and even
		// getEditors(virtualVile) return only one of them... So shotgun approach here.
		FileEditor[] editors = fem.getAllEditors();
		for(FileEditor editor: editors) {
			inject(editor);
		}

		freeUnusedPanels(fem);
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
			logger.debug("I01: Injection failed, only text editors are supported currently.");
			return;
		}

		try {
			JPanel outerPanel = (JPanel)editor.getComponent();
			BorderLayout outerLayout = (BorderLayout)outerPanel.getLayout();
			JLayeredPane pane = (JLayeredPane)outerLayout.getLayoutComponent(BorderLayout.CENTER);
			JPanel panel = (JPanel)pane.getComponent(1);
			BorderLayout innerLayout = (BorderLayout)panel.getLayout();

			// Ok we finally found the actual editor layout. Now make sure we haven't already injected into this editor.
			if(innerLayout.getLayoutComponent(BorderLayout.LINE_END) == null) {
				GlancePanel glancePanel = new GlancePanel(project, editor, panel, runner);
				panel.add(glancePanel, BorderLayout.LINE_END);
				panels.put(editor, glancePanel);
			} else {
				logger.warn("I07: Injection skipped. Looks like we have already injected something here.");
			}
		} catch(ClassCastException e) {
			logger.warn(String.format("Injection failed '%s' on line %d.", e.getMessage(), e.getStackTrace()[0].getLineNumber()));
			return;
		}
	}

	private void uninject(FileEditor editor) {
		if(!(editor instanceof TextEditor)) {
			logger.debug("I01: Uninjection failed, only text editors are supported currently.");
			return;
		}

		try {
			JPanel outerPanel = (JPanel)editor.getComponent();
			BorderLayout outerLayout = (BorderLayout)outerPanel.getLayout();
			JLayeredPane pane = (JLayeredPane)outerLayout.getLayoutComponent(BorderLayout.CENTER);
			JPanel panel = (JPanel)pane.getComponent(1);
			BorderLayout innerLayout = (BorderLayout)panel.getLayout();

			// Ok we finally found the actual editor layout. Now make sure we haven't already injected into this editor.
			Component glancePanel = innerLayout.getLayoutComponent(BorderLayout.LINE_END);
			if (glancePanel != null) {
				panel.remove(glancePanel);
			}
		} catch(ClassCastException e) {
			logger.warn(String.format("Uninjection failed '%s' on line %d.", e.getMessage(), e.getStackTrace()[0].getLineNumber()));
			return;
		}
	}

	@Override
	public void fileClosed(FileEditorManager fem, VirtualFile virtualFile) {
		freeUnusedPanels(fem);
	}

	// Again we don't know which EDITOR was closed, just the file - which could be shared between some editors that
	// are still open. Lets play 'spot the missing editor'.

	/**
	 * On close we dont know which (if any) editor was closed, just the file. And in configurations we dont even
	 * get a fileClosed event. Lets just scan all of the open penels and make sure they are still being used by
	 * at least one of the open editors.
	 */
	private void freeUnusedPanels(FileEditorManager fem) {
		Set<FileEditor> unseen = new HashSet<FileEditor>(panels.keySet());

		for(FileEditor editor: fem.getAllEditors()) {
			if (unseen.contains(editor)) {
				unseen.remove(editor);
			}
		}

		GlancePanel panel;
		for (FileEditor editor: unseen) {
			panel = panels.get(editor);
			panel.onClose();
			uninject(editor);
			panels.remove(editor);
		}
	}

	@Override
	public void selectionChanged(FileEditorManagerEvent fileEditorManagerEvent) { }
}
