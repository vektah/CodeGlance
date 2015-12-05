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
import com.intellij.openapi.editor.FoldRegion;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.psi.tree.IElementType;
import net.vektah.codeglance.config.Config;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * A rendered minimap of a document
 */
public class Minimap {
	public BufferedImage img;
	public int height;
	private Logger logger = Logger.getInstance(getClass());
	private ArrayList<Integer> line_endings;
	private Config config;
	private static final Composite CLEAR = AlphaComposite.getInstance(AlphaComposite.CLEAR);
	private static final int[] unpackedColor = new int[4];
    private static final LineInfo NO_LINES = new LineInfo(1, 0, 0);

	public Minimap(Config config) {
		this.config = config;
	}

	/**
	 * Scans over the entire document once to work out the required dimensions then rebuilds the image if necessary.
	 *
	 * Because java chars are UTF-8 16 bit chars this function should be UTF safe in the 2 byte range, which is all intellij
	 * seems to handle anyway....
	 */
	public void updateDimensions(CharSequence text, FoldRegion[] folding) {
		int line_length = 0;    // The current line length
		int longest_line = 1;   // The longest line in the document
		int lines = 1;          // The total number of lines in the document
		char last = 0;
		char ch;

		ArrayList<Integer> line_endings = new ArrayList<Integer>();
		// Magical first line
		line_endings.add(-1);

		for (int i = 0, len = text.length(); i < len; i++) {
			if(isFolded(i, folding)) {
				continue;
			}

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

		this.line_endings = line_endings;
		height = (lines + 1) * config.pixelsPerLine;

		// If the image is too small to represent the entire document now then regenerate it
		// TODO: Copy old image when incremental update is added.
		if (img == null || img.getHeight() < height || img.getWidth() < config.width) {
			if(img != null) img.flush();
			// Create an image that is a bit bigger then the one we need so we don't need to re-create it again soon.
			// Documents can get big, so rather then relative sizes lets just add a fixed amount on.
			img = new BufferedImage(config.width, height + 100 * config.pixelsPerLine, BufferedImage.TYPE_4BYTE_ABGR);
			logger.debug("Created new image");
		}
	}

	/**
	 * @return the offset that a line starts at within the file.
	 */
	public int getOffsetForLine(int line) {
		if (line < 1) {
			return line_endings.get(1);
		}

		if (line >= line_endings.size()) {
			return line_endings.get(line_endings.size() - 1);
		}

		return line_endings.get(line);
	}

	/**
	 * Binary search for a line ending.
	 * @param i character offset from start of document
	 * @return 3 element array, [line_number, o]
	 */
	public LineInfo getLine(int i) {
		// We can get called before the line scan has been done. Just return the first line.
		if(line_endings == null) return NO_LINES;
		if(line_endings.size() == 0) return NO_LINES;
		int lines = line_endings.get(line_endings.size() - 1);
		if(i > lines) i = lines;
		if(i < 0) i = 0;
		// Dummy entries if there are no lines
		if(line_endings.size() == 0) return NO_LINES;
		if(line_endings.size() == 1) return NO_LINES;
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
			TextAttributes attr = colorScheme.getAttributes(attribute);
			if(attr != null) {
				tmp = attr.getForegroundColor();
				if(tmp != null) color = tmp.getRGB();
			}
		}

		return color;
	}

	/**
	 * Checks if a given position is within a folded region
	 * @param position  the offset from the start of file in chars
	 * @param regions   the array of regions to check against
	 * @return true if the given position is folded.
	 */
	private boolean isFolded(int position, FoldRegion[] regions) {
		for (FoldRegion region: regions) {
			if (!region.isExpanded() && region.getStartOffset() < position && position < region.getEndOffset()) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Internal worker function to update the minimap image
	 *
	 * @param text          The entire text of the document to render
	 * @param colorScheme   The users color scheme
	 * @param hl            The syntax highlighter to use for the language this document is in.
	 */
	public void update(CharSequence text, EditorColorsScheme colorScheme, SyntaxHighlighter hl, FoldRegion[] folding) {
		logger.debug("Updating file image.");
		updateDimensions(text, folding);

		int color;
		int bgcolor = colorScheme.getDefaultBackground().getRGB();
		char ch;
		LineInfo startLine;
		float topWeight;
		float bottomWeight;
		Lexer lexer = hl.getHighlightingLexer();
		IElementType tokenType;

		Graphics2D g = (Graphics2D)img.getGraphics();
		g.setComposite(CLEAR);
		g.fillRect(0, 0, img.getWidth(), img.getHeight());

		lexer.start(text);
		tokenType = lexer.getTokenType();

		int x, y;
		while(tokenType != null) {
			int start = lexer.getTokenStart();
			startLine = getLine(start);
			y = startLine.number * config.pixelsPerLine;

			color = getColorForElementType(tokenType, hl, colorScheme);

			// Pre-loop to count whitespace from start of line.
			x = 0;
			for (int i = startLine.begin; i < start; i++) {
				// Dont count lines inside of folded regions.
				if (isFolded(i, folding)) {
					continue;
				}

				if(text.charAt(i) == '\t') {
					x += 4;
				} else {
					x += 1;
				}

				// Abort if this line is getting to long...
				if(x > config.width) break;
			}

			// Render whole token, make sure multi lines are handled gracefully.
			for(int i = start; i < lexer.getTokenEnd(); i++) {
				// Don't render folds.
				if (isFolded(i, folding)) {
					continue;
				}

				ch = text.charAt(i);

				if(ch == '\n') {
					x = 0;
					y += config.pixelsPerLine;
				} else if(ch == '\t') {
					x += 4;
				} else {
					x += 1;
				}

				topWeight = CharacterWeight.getTopWeight(text.charAt(i));
				bottomWeight = CharacterWeight.getBottomWeight(text.charAt(i));

				// No point rendering non visible characters.
				if(topWeight == 0) continue;

				if(0 <= x && x < img.getWidth() && 0 <= y && y + config.pixelsPerLine < img.getHeight()) {
					switch(config.pixelsPerLine) {
						case 1:
							// Cant show whitespace between lines any more. This looks rather ugly...
							setPixel(x,  y + 1, color, (float) ((topWeight + bottomWeight) / 2.0));
							break;

						case 2:
							// Two lines we make the top line a little lighter to give the illusion of whitespace between lines.
							setPixel(x, y, color, topWeight * 0.5f);
							setPixel(x, y + 1, color, bottomWeight);
							break;
						case 3:
							// Three lines we make the top nearly empty, and fade the bottom a little too
							setPixel(x, y, color, topWeight * 0.3f);
							setPixel(x, y + 1, color, (float) ((topWeight + bottomWeight) / 2.0));
							setPixel(x, y + 2, color, bottomWeight * 0.7f);
							break;
						case 4:
							// Empty top line, Nice blend for everything else
							setPixel(x, y + 1, color, topWeight);
							setPixel(x, y + 2, color, (float) ((topWeight + bottomWeight) / 2.0));
							setPixel(x, y + 3, color, bottomWeight);
					}
				}
			}

			lexer.advance();
			tokenType = lexer.getTokenType();
		}
	}

	/**
	 * mask out the alpha component and set it to the given value.
	 * @param color         Color A
	 * @param alpha     alpha percent from 0-1.
	 * @return int color
	 */
	private void setPixel(int x, int y, int color, float alpha) {
		if(alpha > 1) alpha = color;
		if(alpha < 0) alpha = 0;

		// abgr is backwards?
		unpackedColor[3] = (int) (alpha * 255);
		unpackedColor[0] = (color & 0xFF0000) >> 16;
		unpackedColor[1] = (color & 0x00FF00) >> 8;
		unpackedColor[2] = (color & 0x0000FF);

		img.getRaster().setPixel(x, y, unpackedColor);
	}

	public static class LineInfo {
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
