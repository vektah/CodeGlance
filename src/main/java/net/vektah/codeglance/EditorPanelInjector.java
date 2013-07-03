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
		JComponent component = fileEditorManager.getSelectedEditor(virtualFile).getComponent();

		FileEditor editor = fileEditorManager.getSelectedEditor(virtualFile);
		if(!(editor instanceof TextEditor)) {
			logger.info("Only text editors are supported currently.");
			return;
		}

		if(component instanceof JPanel) {
			JPanel impl = (JPanel)component;
			if(impl.getLayout() instanceof BorderLayout) {
				GlancePanel panel = new GlancePanel(project, editor, impl, runner);
				impl.add(panel, BorderLayout.LINE_END);;
				logger.debug("Injected a new editor panel!");
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
