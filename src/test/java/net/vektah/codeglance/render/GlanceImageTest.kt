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

import com.intellij.openapi.editor.FoldRegion
import com.intellij.openapi.util.text.StringUtil
import net.vektah.codeglance.config.Config
import org.testng.annotations.BeforeMethod
import org.testng.annotations.DataProvider
import org.testng.annotations.Test

import junit.framework.Assert.assertEquals

class GlanceImageTest {
    private var img: Minimap? = null
    private val config = Config()

    @BeforeMethod fun setUp() {
        config.pixelsPerLine = 2
        img = Minimap(config)
    }

    @Test(dataProvider = "Test-Dimensions") fun test_calculate_dimensions(string: CharSequence, height: Int) {
        img!!.updateDimensions(string, Folds())
        assertEquals(height, img!!.height)
    }

    @Test fun test_calculate_dimensions_resize() {
        img!!.updateDimensions("ASDF\nHJKL", Folds())

        assertEquals(config.width, img!!.img!!.width)
        assertEquals(206, img!!.img!!.height)

        // Only added a little, so image should not get regenerated.
        img!!.updateDimensions("asdfjkl;asdfjkl;\nasdfjlkasdfjkl\nasdfjkl;a;sdfjkl", Folds())

        assertEquals(config.width, img!!.img!!.width)
        assertEquals(206, img!!.img!!.height)

        // Went over the existing image boundary so a new one should be created.
        img!!.updateDimensions(StringUtil.repeat("\na", 152), Folds())

        assertEquals(config.width, img!!.img!!.width)
        assertEquals(508, img!!.img!!.height)
    }

    @Test(dataProvider = "Test-Newlines") fun test_newline_search(input: CharSequence, i: Int, expected_number: Int, expected_begin: Int, expected_end: Int) {
        img!!.updateDimensions(input, Folds())

        val line = img!!.getLine(i)

        assertEquals(expected_number, line.number)
        assertEquals(expected_begin, line.begin)
        assertEquals(expected_end, line.end)
    }

    companion object {

        @DataProvider(name = "Test-Dimensions") fun testDimensions(): Array<Array<Any>> {
            return arrayOf(arrayOf("", 4), arrayOf("SingleLine", 4), arrayOf("Multi\nLine", 6), arrayOf("Line with lots of tabs\n\t\t\t\t\t\t\t\t", 6), arrayOf("ʳʳʳʳ", 4), arrayOf("ꬉꬉꬉꬉ", 4))
        }

        @DataProvider(name = "Test-Newlines") fun testNewlines(): Array<Array<Any>> {
            return arrayOf(arrayOf("", 0, 1, 0, 0), arrayOf("1111111111\n2222222222", 0, 1, 0, 10), // First line
                    arrayOf("1111111111\n2222222222", 5, 1, 0, 10), // First line
                    arrayOf("1111111111\n2222222222", 10, 1, 0, 10), // The newline itself
                    arrayOf("1111111111\n2222222222", 15, 2, 11, 20), // The next line, no trailing new line
                    arrayOf("1111111111\n2222222222\n", 15, 2, 11, 21), // The next line with trailing newline.
                    arrayOf("1111111111\n2222222222\n3333333333", 15, 2, 11, 21), // Middle
                    arrayOf("111 111 11\n222 222 22\n333 333 33", 25, 3, 22, 31), // End of line, but truncated to a valid char (no trailing newline)
                    arrayOf("\n\n\n\n", -1, 1, 0, 0), arrayOf("\n\n\n\n", 0, 1, 0, 0), arrayOf("\n\n\n\n", 1, 2, 1, 1), arrayOf("\n\n\n\n", 2, 3, 2, 2), arrayOf("\n\n\n\n", 3, 4, 3, 3), arrayOf("\n\n\n\n", 4, 4, 3, 3))
        }
    }
}
