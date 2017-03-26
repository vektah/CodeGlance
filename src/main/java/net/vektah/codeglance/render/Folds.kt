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

// Is a copy of Array<FoldRegion> that only contains folded folds and can be passed safely to another thread
class Folds{
    val folds: IntArray

    constructor(allFolds: Array<FoldRegion>) {
        val numFolds = allFolds.count { !it.isExpanded }

        folds = IntArray(numFolds*2)

        var i = 0
        allFolds.forEach {
            if (!it.isExpanded) {
                folds[i++] = it.startOffset
                folds[i++] = it.endOffset
            }
        }
    }

    // Used by tests that want an empty fold set
    constructor() {
        folds = intArrayOf()
    }

    /**
     * Checks if a given position is within a folded region
     * @param position  the offset from the start of file in chars
     * *
     * @return true if the given position is folded.
     */
    fun isFolded(position: Int): Boolean {
        for (i in 0..folds.size - 1 step 2) {
            if (folds[i] <= position && position < folds[i+1]) {
                return true
            }
        }

        return false
    }
}
