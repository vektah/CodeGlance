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

package net.vektah.codeglance.config

import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.options.ConfigurationException
import org.jetbrains.annotations.Nls

import javax.swing.*

class ConfigEntry : Configurable {
    private var form: ConfigForm? = null
    private val configService = ServiceManager.getService(ConfigService::class.java)
    private val config = configService.state!!

    @Nls
    override fun getDisplayName(): String {
        return "CodeGlance"
    }

    override fun getHelpTopic(): String? {
        return "Configuration for the CodeGlance minimap"
    }

    override fun createComponent(): JComponent? {
        form = ConfigForm()
        reset()
        return form!!.root
    }

    override fun isModified() = form != null &&
        (config.pixelsPerLine != form!!.pixelsPerLine
            || config.disabled != form!!.isDisabled
            || config.locked != form!!.isLocked
            || config.jumpOnMouseDown != form!!.jumpOnMouseDown()
            || config.percentageBasedClick != form!!.percentageBasedClick()
            || config.width != form!!.width
            || config.viewportColor !== form!!.viewportColor
            || config.bgColor !== form!!.bgColor
            || config.bgColorEnabled != form!!.bgColorEnabled
            || config.dividerColor !== form!!.dividerColor
            || config.dividerColorEnabled != form!!.dividerColorEnabled
            || config.minLineCount != form!!.minLinesCount
            || config.minWindowWidth != form!!.minWindowWidth
            || config.clean != form!!.cleanStyle
            || config.isRightAligned != form!!.isRightAligned)

    @Throws(ConfigurationException::class)
    override fun apply() {
        if (form == null) return

        config.pixelsPerLine = form!!.pixelsPerLine
        config.disabled = form!!.isDisabled
        config.locked = form!!.isLocked
        config.jumpOnMouseDown = form!!.jumpOnMouseDown()
        config.percentageBasedClick = form!!.percentageBasedClick()
        config.width = form!!.width

        if (form!!.viewportColor.length == 6 && form!!.viewportColor.matches("^[a-fA-F0-9]*$".toRegex())) {
            config.viewportColor = form!!.viewportColor
        } else {
            config.viewportColor = "A0A0A0"
        }
        if (form!!.bgColor.length == 6 && form!!.bgColor.matches("^[a-fA-F0-9]*$".toRegex())) {
            config.bgColor = form!!.bgColor
        } else {
            config.bgColor = "303030"
        }
        if (form!!.dividerColor.length == 6 && form!!.dividerColor.matches("^[a-fA-F0-9]*$".toRegex())) {
            config.dividerColor = form!!.dividerColor
        } else {
            config.dividerColor = "FFFFFF"
        }

        config.bgColorEnabled = form!!.bgColorEnabled
        config.dividerColorEnabled = form!!.dividerColorEnabled
        config.minLineCount = form!!.minLinesCount
        config.minWindowWidth = form!!.minWindowWidth
        config.clean = form!!.cleanStyle
        config.isRightAligned = form!!.isRightAligned
        configService.notifyChange()
    }

    override fun reset() {
        if (form == null) return

        form!!.pixelsPerLine = config.pixelsPerLine
        form!!.isDisabled = config.disabled
        form!!.isLocked = config.locked
        form!!.setJumpOnMouseDown(config.jumpOnMouseDown)
        form!!.setPercentageBasedClick(config.percentageBasedClick)
        form!!.viewportColor = config.viewportColor
        form!!.bgColor = config.bgColor
        form!!.bgColorEnabled = config.bgColorEnabled
        form!!.dividerColor = config.dividerColor
        form!!.dividerColorEnabled = config.dividerColorEnabled
        form!!.width = config.width
        form!!.minLinesCount = config.minLineCount
        form!!.minWindowWidth = config.minWindowWidth
        form!!.setCleanStyle(config.clean)
        form!!.setRightAligned(config.isRightAligned)
    }

    override fun disposeUIResources() {
        form = null
    }
}
