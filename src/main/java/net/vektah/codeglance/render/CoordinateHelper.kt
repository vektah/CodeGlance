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

package net.vektah.codeglance.render

import java.awt.*


class CoordinateHelper {
    private var panelHeight = 0
    private var panelWidth = 0
    private var hidpiScale = 1.0f
    private var srcHeight = 0
    private var pixelsPerLine = 2
    private var imageHeight: Int = 0
    private var map: Minimap? = null
    /**
     * @return how far through the current document the user is as a percentage (0-1)
     */
    var percentComplete: Double = 0.toDouble()
        private set

    fun setPixelsPerLine(pixelsPerLine: Int): CoordinateHelper {
        this.pixelsPerLine = pixelsPerLine

        return this
    }

    fun setPanelHeight(panelHeight: Int): CoordinateHelper {
        this.panelHeight = panelHeight

        return this
    }

    fun setPanelWidth(panelWidth: Int): CoordinateHelper {
        this.panelWidth = panelWidth

        return this
    }

    fun setMinimap(map: Minimap): CoordinateHelper {
        this.map = map
        this.imageHeight = map.height

        return this
    }

    fun setHidpiScale(hidpiScale: Float): CoordinateHelper {
        this.hidpiScale = hidpiScale

        return this
    }

    fun setPercentageComplete(complete: Double): CoordinateHelper {
        this.percentComplete = complete

        return this
    }

    // If the panel is 1:1 then just draw everything in the top left hand corner, otherwise we need to gracefully scroll.
    val offset: Int
        get() {
            if (imageHeight > panelHeight * hidpiScale) {
                return ((imageHeight - panelHeight * hidpiScale) * percentComplete).toInt()
            } else {
                return 0
            }
        }

    val imageSource: Rectangle
        get() {
            val offset = offset
            val end = Math.min(offset + panelHeight * hidpiScale, imageHeight.toFloat()).toInt()
            srcHeight = end - offset
            return Rectangle(0, offset, panelWidth, end)
        }

    /**
     * Calculates the coordinates to draw the image onto within the frame. Make sure getImageSource has been called first!
     */
    val imageDestination: Rectangle
        get() = Rectangle(0, 0, panelWidth, Math.min(srcHeight, panelHeight))

    fun getViewport(firstVisibleLine: Int, lastVisibleLine: Int): Rectangle {
        return Rectangle(
                0,
                ((firstVisibleLine * pixelsPerLine - offset) / hidpiScale).toInt(),
                panelWidth - 1,
                ((lastVisibleLine - firstVisibleLine) * pixelsPerLine / hidpiScale).toInt())
    }

    /**
     * Offset: The character offset from the start of file.
     * LogicalPosition: This is the actual position within the document.
     * ScreenSpace: Raw position the user can see. This is a scrolling window for long documents!
     */
    fun screenSpaceToOffset(y: Int, dragged: Boolean): Int {
        var y = y
        if (y < 0) y = 0
        if (y > panelHeight) y = panelHeight
        val line: Int

        if (imageHeight < panelHeight) {
            // If the panel is short enough to fit on the screen then 1:1 is good.
            line = (y / pixelsPerLine * hidpiScale).toInt()
        } else if (dragged) {
            // If we are dragging, then act like a conventional scroll bar.
            line = (y / panelHeight.toFloat() * imageHeight).toInt() / pixelsPerLine
        } else {
            // Otherwise 1:1 with an offset so that clicks in long documents line up correctly.
            line = ((y + offset) / pixelsPerLine * hidpiScale).toInt()
        }

        if (map == null) return line * pixelsPerLine

        return map!!.getOffsetForLine(line)
    }

    fun offsetToScreenSpace(offset: Int): Int {
        if (map == null) return offset / pixelsPerLine

        val line = map!!.getLine(offset).number

        if (imageHeight < panelHeight) {
            return (line.toFloat() * pixelsPerLine.toFloat() * hidpiScale).toInt()
        } else {
            return (line.toFloat() * pixelsPerLine.toFloat() * hidpiScale - offset).toInt()
        }
    }

    fun offsetToCharacterInLine(offset: Int): Int {
        if (map == null) return offset / pixelsPerLine

        return offset - map!!.getLine(offset).begin
    }

    fun linesToPixels(lines: Int): Int {
        return (lines.toFloat() * pixelsPerLine.toFloat() * hidpiScale).toInt()
    }

    fun pixelsToLines(pixels: Int): Int {
        if (imageHeight < panelHeight) {
            return (pixels / pixelsPerLine * hidpiScale).toInt()
        } else {
            return (pixels.toFloat() / pixelsPerLine.toFloat() / (panelHeight / imageHeight.toFloat()) * hidpiScale).toInt()
        }
    }
}
