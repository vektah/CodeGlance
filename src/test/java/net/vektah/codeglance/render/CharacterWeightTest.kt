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

import org.testng.annotations.DataProvider
import org.testng.annotations.Test

import org.testng.Assert.*

/**
 * Some basic sanity tests that the weight generation function works OK.
 */
class CharacterWeightTest {
    @Test fun test_lower_boundaries() {
        assertEquals(0.0f, GetTopWeight(0), 0.001f)
        assertEquals(0.0f,GetTopWeight(1), 0.001f)
        assertEquals(0.0f,GetTopWeight(32), 0.001f)
        assertNotEquals(0.0f,GetTopWeight(33))
        assertNotEquals(0.0f,GetTopWeight(127))
        assertNotEquals(0.0f,GetTopWeight(128))
    }

    @Test(dataProvider = "Test-Relative-Weights") fun test_relative_weights_are_sane(a: Char, b: Char) {
        assertTrue(GetTopWeight(a.toInt()) + GetBottomWeight(a.toInt()) < GetTopWeight(b.toInt()) + GetBottomWeight(b.toInt()))
    }

    @Test fun test_known_values() {
        assertEquals(0.2458f, GetTopWeight('v'.toInt()))
        assertEquals(0.3538f, GetBottomWeight('v'.toInt()))
    }

    companion object {

        @DataProvider(name = "Test-Relative-Weights") fun testRelativeWeights(): Array<Array<Any>> {
            return arrayOf(
                arrayOf<Any>('.', ','),
                arrayOf<Any>('1', '8'),
                arrayOf<Any>('.', 'a'),
                arrayOf<Any>(',', '1')
            )
        }
    }
}
