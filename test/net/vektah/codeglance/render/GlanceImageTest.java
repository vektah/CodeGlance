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

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static junit.framework.Assert.*;

public class GlanceImageTest {
	@DataProvider(name="Test-Dimensions") public static Object[][] testDimensions() {
		return new Object[][] {
			{"", 1, 1},
			{"SingleLine", 10, 1},
			{"Multi\nLine", 5, 2},
			{"Line with lots of tabs\n\t\t\t\t\t\t\t\t", 8*4, 2},
			{"ʳʳʳʳ", 4, 1},
			{"ꬉꬉꬉꬉ", 4, 1},
		};
	}

	@Test(dataProvider = "Test-Dimensions") public void test_calculate_dimensions(CharSequence string, int width, int height) {
		Minimap img = new Minimap();

		img.updateDimensions(string);
		assertEquals(width, img.width);
		assertEquals(height, img.height);
	}

	@Test public void test_calculate_dimensions_resize() {
		Minimap img = new Minimap();

		img.updateDimensions("ASDF\nHJKL");

		assertEquals(104, img.img.getWidth());
		assertEquals(202, img.img.getHeight());

		// Only added a little, so image should not get regenerated.
		img.updateDimensions("asdfjkl;asdfjkl;\nasdfjlkasdfjkl\nasdfjkl;a;sdfjkl");

		assertEquals(104, img.img.getWidth());
		assertEquals(202, img.img.getHeight());

		// Went over the existing image boundary so a new one should be created.
		img.updateDimensions("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");

		assertEquals(220, img.img.getWidth());
		assertEquals(201, img.img.getHeight());
	}

	@DataProvider(name="Test-Newlines") public static Object[][] testNewlines() {
		return new Object[][] {
			{"", 0, 1, 0, 0},
			{"1111111111\n2222222222", 0, 1, 0, 10},        // First line
			{"1111111111\n2222222222", 5, 1, 0, 10},        // First line
			{"1111111111\n2222222222", 10, 1, 0, 10},       // The newline itself
			{"1111111111\n2222222222", 15, 2, 11, 20},       // The next line, no trailing new line
			{"1111111111\n2222222222\n", 15, 2, 11, 21},       // The next line with trailing newline.
			{"1111111111\n2222222222\n3333333333", 15, 2, 11, 21},   // Middle
			{"111 111 11\n222 222 22\n333 333 33", 25, 3, 22, 31},   // End of line, but truncated to a valid char (no trailing newline)
			{"\n\n\n\n", 0, 1, 0, 0},
			{"\n\n\n\n", 1, 2, 1, 1},
			{"\n\n\n\n", 2, 3, 2, 2},
			{"\n\n\n\n", 3, 4, 3, 3},

		};
	}

	@Test(dataProvider = "Test-Newlines")  public void test_newline_search(CharSequence input, int i, int expected_number, int expected_begin, int expected_end) {
		Minimap img = new Minimap();

		img.updateDimensions(input);

		Minimap.LineInfo line = img.getLine(i);

		assertEquals(expected_number, line.number);
		assertEquals(expected_begin, line.begin);
		assertEquals(expected_end, line.end);
	}
}
