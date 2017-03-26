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

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.FoldRegion
import com.intellij.openapi.editor.colors.EditorColorsScheme
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.psi.tree.IElementType
import net.vektah.codeglance.config.Config

import java.awt.*
import java.awt.image.BufferedImage
import java.util.ArrayList

/**
 * A rendered minimap of a document
 */
class Minimap(private val config: Config) {
    var img: BufferedImage? = null
    var height: Int = 0
    private val logger = Logger.getInstance(javaClass)
    private var line_endings: ArrayList<Int>? = null

    /**
     * Scans over the entire document once to work out the required dimensions then rebuilds the image if necessary.

     * Because java chars are UTF-8 16 bit chars this function should be UTF safe in the 2 byte range, which is all intellij
     * seems to handle anyway....
     */
    fun updateDimensions(text: CharSequence, folds: Folds) {
        var line_length = 0    // The current line length
        var longest_line = 1   // The longest line in the document
        var lines = 1          // The total number of lines in the document
        var last: Char = ' '
        var ch: Char

        val line_endings = ArrayList<Int>()
        // Magical first line
        line_endings.add(-1)

        var i = 0
        val len = text.length
        while (i < len) {
            if (folds.isFolded(i)) {
                i++
                continue
            }

            ch = text[i]

            if (ch == '\n' || (ch == '\r' && last != '\n')) {
                line_endings.add(i)
                lines++
                if (line_length > longest_line) longest_line = line_length
                line_length = 0
            } else if (ch == '\t') {
                line_length += 4
            } else {
                line_length++
            }

            last = ch
            i++
        }
        // If there is no final newline add one.
        if (line_endings[line_endings.size - 1] !== text.length - 1) line_endings.add(text.length - 1)

        this.line_endings = line_endings
        height = (lines + 1) * config.pixelsPerLine

        // If the image is too small to represent the entire document now then regenerate it
        // TODO: Copy old image when incremental update is added.
        if (img == null || img!!.height < height || img!!.width < config.width) {
            if (img != null) img!!.flush()
            // Create an image that is a bit bigger then the one we need so we don't need to re-create it again soon.
            // Documents can get big, so rather then relative sizes lets just add a fixed amount on.
            img = BufferedImage(config.width, height + 100 * config.pixelsPerLine, BufferedImage.TYPE_4BYTE_ABGR)
            logger.debug("Created new image")
        }
    }

    /**
     * Binary search for a line ending.
     * @param i character offset from start of document
     * *
     * @return 3 element array, [line_number, o]
     */
    fun getLine(i: Int): LineInfo {
        // We can get called before the line scan has been done. Just return the first line.
        if (line_endings == null) return NO_LINES
        if (line_endings!!.size == 0) return NO_LINES
        val lines = line_endings!![line_endings!!.size - 1]

        // Dummy entries if there are no lines
        if (line_endings!!.size == 0) return NO_LINES
        if (line_endings!!.size == 1) return NO_LINES
        if (line_endings!!.size == 2) return LineInfo(1, line_endings!![0] + 1, line_endings!![1])

        var index_min = 0
        var index_max = line_endings!!.size - 1
        var index_mid: Int
        var value: Int

        val clampedI = clamp(i, 0, lines)

        while (true) {
            index_mid = Math.floor(((index_min + index_max) / 2.0f).toDouble()).toInt() // Key space is pretty linear, might be able to use that to scale our next point.
            value = line_endings!![index_mid]

            if (value < clampedI) {
                if (clampedI < line_endings!![index_mid + 1]) return LineInfo(index_mid + 1, value + 1, line_endings!![index_mid + 1])

                index_min = index_mid + 1
            } else if (clampedI < value) {
                if (line_endings!![index_mid - 1] < clampedI) return LineInfo(index_mid, line_endings!![index_mid - 1] + 1, value)

                index_max = index_mid - 1
            } else {
                // character at i is actually a newline, so grab the line before it.
                return LineInfo(index_mid, line_endings!![index_mid - 1] + 1, clampedI)
            }
        }
    }

    /**
     * Works out the color a token should be rendered in.

     * @param element       The element to get the color for
     * *
     * @param hl            the syntax highlighter for this document
     * *
     * @param colorScheme   the users color scheme
     * *
     * @return the RGB color to use for the given element
     */
    private fun getColorForElementType(element: IElementType, hl: SyntaxHighlighter, colorScheme: EditorColorsScheme): Int {
        var color = colorScheme.defaultForeground.rgb
        var tmp: Color?
        val attributes = hl.getTokenHighlights(element)
        for (attribute in attributes) {
            val attr = colorScheme.getAttributes(attribute)
            if (attr != null) {
                tmp = attr.foregroundColor
                if (tmp != null) color = tmp.rgb
            }
        }

        return color
    }

    /**
     * Internal worker function to update the minimap image

     * @param text          The entire text of the document to render
     * *
     * @param colorScheme   The users color scheme
     * *
     * @param hl            The syntax highlighter to use for the language this document is in.
     */
    fun update(text: CharSequence, colorScheme: EditorColorsScheme, hl: SyntaxHighlighter, folds: Folds) {
        logger.debug("Updating file image.")
        updateDimensions(text, folds)

        var color: Int
        var ch: Char
        var startLine: LineInfo
        val lexer = hl.highlightingLexer
        var tokenType: IElementType?

        val g = img!!.graphics as Graphics2D
        g.composite = CLEAR
        g.fillRect(0, 0, img!!.width, img!!.height)

        lexer.start(text)
        tokenType = lexer.tokenType

        var x: Int
        var y: Int
        while (tokenType != null) {
            val start = lexer.tokenStart
            startLine = getLine(start)
            y = startLine.number * config.pixelsPerLine

            color = getColorForElementType(tokenType, hl, colorScheme)

            // Pre-loop to count whitespace from start of line.
            x = 0
            for (i in startLine.begin..start - 1) {
                // Dont count lines inside of folded regions.
                if (folds.isFolded(i)) {
                    continue
                }

                if (text[i] == '\t') {
                    x += 4
                } else {
                    x += 1
                }

                // Abort if this line is getting to long...
                if (x > config.width) break
            }

            // Render whole token, make sure multi lines are handled gracefully.
            for (i in start..lexer.tokenEnd - 1) {
                // Don't render folds.
                if (folds.isFolded(i)) continue
                // Watch out for tokens that extend past the document... bad plugins? see issue #138
                if (i >= text.length) return

                ch = text[i]

                if (ch == '\n') {
                    x = 0
                    y += config.pixelsPerLine
                } else if (ch == '\t') {
                    x += 4
                } else {
                    x += 1
                }

                if (0 <= x && x < img!!.width && 0 <= y && y + config.pixelsPerLine < img!!.height) {
                    if (config.clean) {
                        renderClean(x, y, text[i].toInt(), color)
                    } else {
                        renderAccurate(x, y, text[i].toInt(), color)
                    }
                }
            }

            lexer.advance()
            tokenType = lexer.tokenType
        }
    }

    private fun renderClean(x: Int, y: Int, char: Int, color: Int) {
        val weight = when (char) {
            in 0..32 -> 0.0f
            in 33..126 -> 0.8f
            else -> 0.4f
        }

        if (weight == 0.0f) return

        when (config.pixelsPerLine) {
            1 -> // Cant show whitespace between lines any more. This looks rather ugly...
                setPixel(x, y + 1, color, weight * 0.6f)

            2 -> {
                // Two lines we make the top line a little lighter to give the illusion of whitespace between lines.
                setPixel(x, y, color, weight * 0.3f)
                setPixel(x, y + 1, color, weight * 0.6f)
            }
            3 -> {
                // Three lines we make the top nearly empty, and fade the bottom a little too
                setPixel(x, y, color, weight * 0.1f)
                setPixel(x, y + 1, color, weight * 0.6f)
                setPixel(x, y + 2, color, weight * 0.6f)
            }
            4 -> {
                // Empty top line, Nice blend for everything else
                setPixel(x, y + 1, color, weight * 0.6f)
                setPixel(x, y + 2, color, weight * 0.6f)
                setPixel(x, y + 3, color, weight * 0.6f)
            }
        }
    }

    private fun renderAccurate(x: Int, y: Int, char: Int, color: Int) {
        val topWeight = GetTopWeight(char)
        val bottomWeight = GetBottomWeight(char)
        // No point rendering non visible characters.
        if (topWeight == 0.0f && bottomWeight == 0.0f) return

        when (config.pixelsPerLine) {
            1 -> // Cant show whitespace between lines any more. This looks rather ugly...
                setPixel(x, y + 1, color, ((topWeight + bottomWeight) / 2.0).toFloat())

            2 -> {
                // Two lines we make the top line a little lighter to give the illusion of whitespace between lines.
                setPixel(x, y, color, topWeight * 0.5f)
                setPixel(x, y + 1, color, bottomWeight)
            }
            3 -> {
                // Three lines we make the top nearly empty, and fade the bottom a little too
                setPixel(x, y, color, topWeight * 0.3f)
                setPixel(x, y + 1, color, ((topWeight + bottomWeight) / 2.0).toFloat())
                setPixel(x, y + 2, color, bottomWeight * 0.7f)
            }
            4 -> {
                // Empty top line, Nice blend for everything else
                setPixel(x, y + 1, color, topWeight)
                setPixel(x, y + 2, color, ((topWeight + bottomWeight) / 2.0).toFloat())
                setPixel(x, y + 3, color, bottomWeight)
            }
        }
    }

    /**
     * mask out the alpha component and set it to the given value.
     * @param color         Color A
     * *
     * @param alpha     alpha percent from 0-1.
     * *
     * @return int color
     */
    private fun setPixel(x: Int, y: Int, color: Int, alpha: Float) {
        var a = alpha
        if (a > 1) a = color.toFloat()
        if (a < 0) a = 0f

        // abgr is backwards?
        unpackedColor[3] = (a * 255).toInt()
        unpackedColor[0] = (color and 16711680) shr 16
        unpackedColor[1] = (color and 65280) shr 8
        unpackedColor[2] = (color and 255)

        img!!.raster.setPixel(x, y, unpackedColor)
    }

    class LineInfo internal constructor(var number: Int, var begin: Int, var end: Int)

    companion object {
        private val CLEAR = AlphaComposite.getInstance(AlphaComposite.CLEAR)
        private val unpackedColor = IntArray(4)
        private val NO_LINES = LineInfo(1, 0, 0)
    }
}
