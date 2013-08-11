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

package net.vektah.codeglance.render;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static junit.framework.Assert.assertEquals;

public class CoordinateHelperTest {
	private CoordinateHelper helper;

	@BeforeMethod public void setUp() {
		helper = new CoordinateHelper();
	}

	@Test public void test_short_file() {
		// 5 lines, editor at start of file.
		helper.setPanelHeight(20).setImageHeight(10);

		assertEquals(2, helper.getPositionFor(0, 5, false).line);
		assertEquals(2, helper.getPositionFor(0, 5, true).line);
	}

	@Test public void test_long_file_at_start() {
		// 100 lines, Image is twice as large as the panel, and the editor is at the start of the file.
		helper.setPanelHeight(100).setImageHeight(200).setFirstVisibleLine(0).setLastVisibleLine(50);

		// Dragging should always be percentage based:
		// Drag 25%, should be 25%
		assertEquals(25, helper.getPositionFor(0, 25, true).line);

		// Drag half way, should be 50%
		assertEquals(50, helper.getPositionFor(0, 50, true).line);

		// Drag 75% should be 75%
		assertEquals(75, helper.getPositionFor(0, 75, true).line);

		// Clicking however, should take you to the line you clicked on.
		// Click 25%, should be 12.5%
		assertEquals(12, helper.getPositionFor(0, 25, false).line);

		// Click 50%, should be 25%
		assertEquals(25, helper.getPositionFor(0, 50, false).line);

		// Click 75%, should be 50%
		assertEquals(37, helper.getPositionFor(0, 75, false).line);
	}

	@Test public void test_long_file_at_end() {
		// 100 lines, Image is twice as large as the panel, and the editor is at the end of the file.
		helper.setPanelHeight(100).setImageHeight(200).setFirstVisibleLine(50).setLastVisibleLine(100);

		// Dragging should always be percentage based:
		// Drag 25%, should be 25%
		assertEquals(25, helper.getPositionFor(0, 25, true).line);

		// Drag half way, should be 50%
		assertEquals(50, helper.getPositionFor(0, 50, true).line);

		// Drag 75% should be 75%
		assertEquals(75, helper.getPositionFor(0, 75, true).line);

		// Clicking however, should take you to the line you clicked on.
		// Click 25%, should be 12.5%
		assertEquals(62, helper.getPositionFor(0, 25, false).line);

		// Click 50%, should be 25%
		assertEquals(75, helper.getPositionFor(0, 50, false).line);

		// Click 75%, should be 50%
		assertEquals(87, helper.getPositionFor(0, 75, false).line);
	}

	@Test public void test_long_file_1_pixel_per_line_at_end() {
		// 100 lines, Image is twice as large as the panel, and the editor is at the end of the file.
		helper.setPanelHeight(100).setImageHeight(200).setPixelsPerLine(1).setFirstVisibleLine(100).setLastVisibleLine(200);

		// Dragging should always be percentage based:
		// Drag 25%, should be 25%
		assertEquals(50, helper.getPositionFor(0, 25, true).line);

		// Drag half way, should be 50%
		assertEquals(100, helper.getPositionFor(0, 50, true).line);

		// Drag 75% should be 75%
		assertEquals(150, helper.getPositionFor(0, 75, true).line);

		// Clicking however, should take you to the line you clicked on.
		// Click 25%, should be 12.5%
		assertEquals(125, helper.getPositionFor(0, 25, false).line);

		// Click 50%, should be 25%
		assertEquals(150, helper.getPositionFor(0, 50, false).line);

		// Click 75%, should be 50%
		assertEquals(175, helper.getPositionFor(0, 75, false).line);
	}

}
