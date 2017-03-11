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

import org.testng.annotations.Test
import org.testng.Assert.*

class ScrollStateTest {

//    @Test fun testEditorArea() {
//        assertEquals(editorArea(RenderInfo(2200, 50, 4), 0, 50), Rectangle(0, 0, 50, 200))
//        assertEquals(editorArea(RenderInfo(2200, 50, 2), 0, 50), Rectangle(0, 0, 50, 100))
//        assertEquals(editorArea(RenderInfo(2200, 50, 2), 250, 300), Rectangle(0, 500, 50, 100))
//        assertEquals(editorArea(RenderInfo(200, 50, 2), 100, 150), Rectangle(0, 200, 50, 0))
//    }
//
    @Test fun testVisibleAreaWhenEntireDocumentIsVisible() {
        val ss = ScrollState()
            .setDocumentSize(50, 100)
            .setViewportArea(0, 100)
            .setVisibleHeight(100)

        assertEquals(ss.visibleStart, 0)
        assertEquals(ss.visibleEnd, 100)
    }

    @Test fun testVisibleAreaInMiddleOfShortDocument() {
        val ss = ScrollState()
            .setDocumentSize(50, 156)
            .setViewportArea(22, 124)
            .setVisibleHeight(600)

        assertEquals(0, ss.visibleStart)
        assertEquals(156, ss.visibleEnd)
    }

    @Test fun testVisibleAreaAtStartOfLongDocument() {
        val ss = ScrollState()
            .setDocumentSize(50, 2000)
            .setViewportArea(0, 100)
            .setVisibleHeight(300)

        assertEquals(0, ss.visibleStart)
        assertEquals(300, ss.visibleEnd)
    }

    @Test fun testVisibleAreaAtEndOfLongDocument() {
        val ss = ScrollState()
            .setDocumentSize(50, 2000)
            .setViewportArea(1950, 50)
            .setVisibleHeight(300)

        assertEquals(1700, ss.visibleStart)
        assertEquals(2000, ss.visibleEnd)
    }
}

