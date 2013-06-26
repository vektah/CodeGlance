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

package net.vektah.codeglance;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.LogicalPosition;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.ui.UIUtil;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Renders an entire file to an image, each character becomes a single pixel with weights dependant on how visible that
 * character is.
 */
public class GlanceFileRender {
	private BufferedImage img;
	private Project project;
	private Editor editor;
	private Logger logger = Logger.getInstance(getClass());

	public GlanceFileRender(Project project, Editor editor) {
		this.project = project;
		this.editor = editor;
		updateImage();

		editor.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void beforeDocumentChange(DocumentEvent documentEvent) {

			}

			@Override
			public void documentChanged(DocumentEvent documentEvent) {
				updateImage();
			}
		});
	}

	public Rectangle getRenderAreaInChars() {
		Rectangle visible = editor.getScrollingModel().getVisibleArea();


		LogicalPosition top_left = editor.xyToLogicalPosition(new Point(visible.x, visible.y));
		LogicalPosition bottom_right = editor.xyToLogicalPosition(new Point(visible.x + visible.width, visible.y + visible.height));

		return new Rectangle(top_left.column, top_left.line, bottom_right.column - top_left.column, bottom_right.line - top_left.line);

	}

	public int getLongestLine() {
		int max = 0;
		for(int i = 0; i < editor.getDocument().getLineCount(); i++) {
			int length = editor.getDocument().getLineEndOffset(i) - editor.getDocument().getLineStartOffset(i);
			if (length > max) {
				max = length;
			}
		}

		return max;
	}

	public void updateImage() {
		int line_count = editor.getDocument().getLineCount();
		int longest_line = getLongestLine();
		Color attribute_color;
		int color;
		int charColor;
		int bgcolor = editor.getColorsScheme().getDefaultBackground().getRGB();
		float weight;

		Document document = editor.getDocument();
		String text = document.getText();

		if (img == null || img.getWidth() < longest_line || img.getHeight() < line_count) {
			img = UIUtil.createImage(longest_line, editor.getDocument().getLineCount(), BufferedImage.TYPE_INT_ARGB);
			logger.info("Created new image");
		}


		Graphics g = img.getGraphics();
		g.setColor(editor.getColorsScheme().getDefaultBackground());
		g.fillRect(0, 0, img.getWidth(), img.getHeight());
		g.dispose();

		int line, lineoffset;

		PsiFile file = PsiDocumentManager.getInstance(project).getPsiFile(editor.getDocument());

		SyntaxHighlighter hl = SyntaxHighlighterFactory.getSyntaxHighlighter(file.getLanguage(), project, file.getVirtualFile());

		Lexer lexer = hl.getHighlightingLexer();

		Logger logger = Logger.getInstance(getClass());
		logger.warn("Regenerating file image.");
		lexer.start(document.getCharsSequence());

		while(true) {
			IElementType tokenType = lexer.getTokenType();

			if(tokenType == null) {
				break;
			}

			color = editor.getColorsScheme().getDefaultForeground().getRGB();
			TextAttributesKey[] attributes = hl.getTokenHighlights(tokenType);
			for(TextAttributesKey attribute : attributes) {
				attribute_color = editor.getColorsScheme().getAttributes(attribute).getForegroundColor();
				if(attribute_color != null) color = attribute_color.getRGB();
			}

			for(int i = lexer.getTokenStart(); i < lexer.getTokenEnd(); i++) {
				weight = CharacterWeight.getWeight(text.charAt(i));
				if(weight == 0) continue;

				charColor = mix(color, bgcolor, weight);

				line = document.getLineNumber(i);
				lineoffset = document.getLineStartOffset(line);

				// Look for tabs, and add four spaces to offset when one is encountered.
				for(int j = lineoffset; j < i; j++) {
					if(text.charAt(j) == '\t') {
						lineoffset-=4;
					}
				}

				img.setRGB(i - lineoffset, line, charColor);
			}

			lexer.advance();
		}
	}

	// TODO: need a better mix function.
	private int mix(int a, int b, float alpha) {
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

	public BufferedImage getImg() {
		return img;
	}
}
