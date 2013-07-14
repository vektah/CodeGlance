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

import com.intellij.lexer.Lexer;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.ui.UIUtil;
import net.vektah.codeglance.GlancePanel;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * A rendered minimap of a document
 */
public class Minimap {
	public BufferedImage img;
	public int height;
	public int width;
	private Logger logger = Logger.getInstance(getClass());
	private ArrayList<Integer> line_endings;

	/**
	 * Scans over the entire document once to work out the required dimensions then rebuilds the image if nessicary.
	 *
	 * Because java chars are UTF-8 16 bit chars this function should be UTF safe in the 2 byte range, which is all intellij
	 * seems to handle anyway....
	 */
	public void updateDimensions(CharSequence text) {
		int line_length = 0;    // The current line length
		int longest_line = 1;   // The longest line in the document
		int lines = 1;          // The total number of lines in the document
		char last = 0;
		char ch;

		line_endings = new ArrayList<Integer>();
		// Magical first line
		line_endings.add(-1);

		for (int i = 0, len = text.length(); i < len; i++) {
			ch = text.charAt(i);

			if(ch == '\n' || (ch == '\r' && last != '\n')) {
				line_endings.add(i);
				lines++;
				if(line_length > longest_line) longest_line = line_length;
				line_length = 0;
			} else if (ch == '\t') {
				line_length += 4;
			} else {
				line_length++;
			}

			last = ch;
		}
		// If there is no final newline add one.
		if(line_endings.get(line_endings.size() - 1) != text.length() - 1) line_endings.add(text.length() - 1);

		if(line_length > longest_line) longest_line = line_length;

		width = Math.min(longest_line, GlancePanel.MAX_WIDTH);    // TODO should be configurable
		height = lines * 2;     // Two pixels per line

		// If the image is too small to represent the entire document now then regenerate it
		// TODO: Copy old image when incremental update is added.
		if (img == null || img.getWidth() < width || img.getHeight() < height) {
			if(img != null) img.flush();
			// Create an image that is a bit bigger then the one we need so we don't need to re-create it again soon.
			// Documents can get big, so rather then relative sizes lets just add a fixed amount on.
			img = new BufferedImage(width, height + 200, BufferedImage.TYPE_3BYTE_BGR);
			logger.debug("Created new image");
		}
	}

	/**
	 * Binary search for a line ending.
	 * @param i character offset from start of document
	 * @return 3 element array, [line_number, o]
	 */
	public LineInfo getLine(int i) {
		int lines = line_endings.get(line_endings.size() - 1);
		if(i > lines) i = lines;
		if(i < 0) i = 0;
		// Dummy entries if there are no lines
		if(line_endings.size() == 0) return new LineInfo(1, 0, 0);
		if(line_endings.size() == 1) return new LineInfo(1, 0, 0);
		if(line_endings.size() == 2) return new LineInfo(1, line_endings.get(0) + 1, line_endings.get(1));

		int index_min = 0;
		int index_max = line_endings.size() - 1;
		int index_mid;
		int value;

		while(true) {
			index_mid = (int) Math.floor((index_min + index_max) / 2.0f); // Key space is pretty linear, might be able to use that to scale our next point.
			value = line_endings.get(index_mid);

			if(value < i) {
				if(i < line_endings.get(index_mid + 1)) return new LineInfo(index_mid + 1, value + 1, line_endings.get(index_mid + 1));

				index_min = index_mid + 1;
			} else if(i < value) {
				if(line_endings.get(index_mid - 1) < i) return new LineInfo(index_mid, line_endings.get(index_mid - 1) + 1, value);

				index_max = index_mid - 1;
			} else {
				// character at i is actually a newline, so grab the line before it.
				return new LineInfo(index_mid, line_endings.get(index_mid - 1) + 1, i);
			}
		}
	}

	/**
	 * Works out the color a token should be rendered in.
	 *
	 * @param element       The element to get the color for
	 * @param hl            the syntax highlighter for this document
	 * @param colorScheme   the users color scheme
	 * @return the RGB color to use for the given element
	 */
	private int getColorForElementType(IElementType element, SyntaxHighlighter hl, EditorColorsScheme colorScheme) {
		int color = colorScheme.getDefaultForeground().getRGB();
		Color tmp;
		TextAttributesKey[] attributes = hl.getTokenHighlights(element);
		for(TextAttributesKey attribute : attributes) {
			tmp = colorScheme.getAttributes(attribute).getForegroundColor();
			if(tmp != null) color = tmp.getRGB();
		}

		return color;
	}

	/**
	 * Internal worker function to update the minimap image
	 *
	 * @param text          The entire text of the document to render
	 * @param colorScheme   The users color scheme
	 * @param hl            The syntax highlighter to use for the language this document is in.
	 */
	public void update(CharSequence text, EditorColorsScheme colorScheme, SyntaxHighlighter hl) {
		logger.debug("Updating file image.");
		updateDimensions(text);

		int color;
		int bgcolor = colorScheme.getDefaultBackground().getRGB();
		char ch;
		LineInfo startLine;
		float weight;
		Lexer lexer = hl.getHighlightingLexer();
		IElementType tokenType;

		Graphics g = img.getGraphics();
		g.setColor(colorScheme.getDefaultBackground());
		g.fillRect(0, 0, img.getWidth(), img.getHeight());
		g.dispose();

		lexer.start(text);
		tokenType = lexer.getTokenType();

		int x, y;
		while(tokenType != null) {
			startLine = getLine(lexer.getTokenStart());
			y = startLine.number * 2;

			color = getColorForElementType(tokenType, hl, colorScheme);

			// Pre-loop to count whitespace from start of line.
			x = 0;
			for(int i = startLine.begin; i < lexer.getTokenStart(); i++) {
				if(text.charAt(i) == '\t') {
					x += 4;
				} else {
					x += 1;
				}
			}

			// Render whole token, make sure multi lines are handled gracefully.
			for(int i = lexer.getTokenStart(); i < lexer.getTokenEnd(); i++) {
				ch = text.charAt(i);

				if(ch == '\n') {
					x = 0;
					y += 2;
				} else if(ch == '\t') {
					x += 4;
				} else {
					x += 1;
				}

				weight = CharacterWeight.getWeight(text.charAt(i));

				// No point rendering non visible characters.
				if(weight == 0) continue;

				if(0 <= x && x < img.getWidth() && 0 <= y && y + 1 <= img.getHeight()) {
					img.setRGB(x, y, mix(color, bgcolor, weight * 0.3f));
					img.setRGB(x, y + 1, mix(color, bgcolor, weight));
				}
			}

			lexer.advance();
			tokenType = lexer.getTokenType();
		}
	}

	/**
	 * Mix two colors together
	 * @param a         Color A
	 * @param b         Color B
	 * @param alpha     alpha percent from 0-1.
	 * @return Mixed color
	 */
	private int mix(int a, int b, float alpha) {
		if(alpha > 1) alpha = a;
		if(alpha < 0) alpha = 0;

		float aR = (a & 0xFF0000) >> 16;
		float aG = (a & 0x00FF00) >> 8;
		float aB = (a & 0x0000FF);

		float bR = (b & 0xFF0000) >> 16;
		float bG = (b & 0x00FF00) >> 8;
		float bB = (b & 0x0000FF);

		int cR = (int) (aR * alpha + bR * (1 - alpha));
		int cG = (int) (aG * alpha + bG * (1 - alpha));
		int cB = (int) (aB * alpha + bB * (1 - alpha));

		return 0xFF000000 | (cR << 16) | (cG << 8) | cB;
	}

	public class LineInfo {
		LineInfo(int number, int begin, int end) {
			this.number = number;
			this.begin = begin;
			this.end = end;
		}

		public int number;
		public int begin;
		public int end;
	}
}
