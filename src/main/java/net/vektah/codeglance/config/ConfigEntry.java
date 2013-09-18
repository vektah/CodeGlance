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

package net.vektah.codeglance.config;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class ConfigEntry implements Configurable {
	private ConfigForm form;
	private ConfigService configService = ServiceManager.getService(ConfigService.class);
	private Config config = configService.getState();

	@Nls @Override public String getDisplayName() {
		return "CodeGlance";
	}

	@Nullable @Override public String getHelpTopic() {
		return "Configuration for the CodeGlance minimap";
	}

	@Nullable @Override public JComponent createComponent() {
		form = new ConfigForm();
		reset();
		return form.getRoot();
	}

	@Override public boolean isModified() {
		if (form == null) return false;

		return config.pixelsPerLine != form.getPixelsPerLine() ||
			config.disabled != form.isDisabled() ||
			config.jumpOnMouseDown != form.jumpOnMouseDown() ||
			config.percentageBasedClick != form.percentageBasedClick();
	}

	@Override public void apply() throws ConfigurationException {
		if(form == null) return;

		config.pixelsPerLine = form.getPixelsPerLine();
		config.disabled = form.isDisabled();
		config.jumpOnMouseDown = form.jumpOnMouseDown();
		config.percentageBasedClick = form.percentageBasedClick();
		configService.dispatch().configChanged();
	}

	@Override public void reset() {
		if(form == null) return;

		form.setPixelsPerLine(config.pixelsPerLine);
		form.setDisabled(config.disabled);
		form.setJumpOnMouseDown(config.jumpOnMouseDown);
		form.setPercentageBasedClick(config.percentageBasedClick);
	}

	@Override public void disposeUIResources() {
		form = null;
	}
}
