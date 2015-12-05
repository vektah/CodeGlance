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

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.project.Project;
import net.vektah.codeglance.config.ConfigService;
import net.vektah.codeglance.render.TaskRunner;
import org.jetbrains.annotations.NotNull;

/**
 * Main plugin
 */
public class CodeGlancePlugin implements ProjectComponent {
	private Project project;
	private Logger logger = Logger.getInstance(getClass());
	private TaskRunner runner = new TaskRunner();
	private Thread runnerThread = new Thread(runner);
	private EditorPanelInjector injector;

	public CodeGlancePlugin(Project project) {
		this.project = project;
		injector = new EditorPanelInjector(project, runner);
	}

	public void initComponent() {
		ServiceManager.getService(ConfigService.class).getState().disabled = false;
		runnerThread.start();
		project.getMessageBus().connect().subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, injector);
		logger.debug("CodeGlance initialized");
	}

	public void disposeComponent() {
		runner.stop();
	}

	@NotNull public String getComponentName() {
		return "CodeGlancePlugin";
	}

	public void projectOpened() {
		// called when project is opened
	}

	public void projectClosed() {
		// called when project is being closed
	}
}
