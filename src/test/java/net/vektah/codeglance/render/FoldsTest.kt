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

class FoldsTest {
    @Test fun testNothingMatchesEmptyFoldSet() {
        val folds = Folds()

        assertFalse(folds.isFolded(0))
        assertFalse(folds.isFolded(-1))
        assertFalse(folds.isFolded(1))
        assertFalse(folds.isFolded(99))
    }

    @Test fun testFoldedRegionMatch() {
        val folds = Folds(arrayOf(FakeFold(10, 20, true)))

        assertFalse(folds.isFolded(0))
        assertTrue(folds.isFolded(10))
        assertTrue(folds.isFolded(15))
        assertFalse(folds.isFolded(20))
        assertFalse(folds.isFolded(25))
    }


    @Test fun testUnfoldedRegionsDontMatch() {
        val folds = Folds(arrayOf(FakeFold(10, 20, false)))

        assertFalse(folds.isFolded(0))
        assertFalse(folds.isFolded(10))
        assertFalse(folds.isFolded(15))
        assertFalse(folds.isFolded(25))
    }

    @Test fun testNestedFoldedRegions() {
        val folds = Folds(arrayOf(
            FakeFold(10, 20, true),
            FakeFold(12, 16, true),
            FakeFold(14, 15, true),
            FakeFold(18, 19, true)
        ))

        assertFalse(folds.isFolded(0))
        assertTrue(folds.isFolded(11))
        assertTrue(folds.isFolded(13))
        assertTrue(folds.isFolded(14))
        assertTrue(folds.isFolded(18))
        assertFalse(folds.isFolded(20))
        assertFalse(folds.isFolded(25))
    }
}
