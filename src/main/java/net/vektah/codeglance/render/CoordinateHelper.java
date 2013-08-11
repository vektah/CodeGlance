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

import com.intellij.openapi.editor.LogicalPosition;

import java.awt.*;

public class CoordinateHelper {
	private int panelHeight = 0;
	private int panelWidth = 0;
	private int imageHeight = 0;
	private int firstVisibleLine = 0;
	private int lastVisibleLine = 0;
	private float hidpiScale = 1.0f;
    private int srcHeight = 0;
	private int pixelsPerLine = 2;

	public CoordinateHelper setPixelsPerLine(int pixelsPerLine) {
		this.pixelsPerLine = pixelsPerLine;

		return this;
	}

	public CoordinateHelper setPanelHeight(int panelHeight) {
		this.panelHeight = panelHeight;

		return this;
	}

	public CoordinateHelper setPanelWidth(int panelWidth) {
		this.panelWidth = panelWidth;

		return this;
	}

	public CoordinateHelper setImageHeight(int imageHeight) {
		this.imageHeight = imageHeight;

		return this;
	}

	public CoordinateHelper setFirstVisibleLine(int firstVisibleLine) {
		this.firstVisibleLine = firstVisibleLine;

		return this;
	}

	public CoordinateHelper setLastVisibleLine(int lastVisibleLine) {
		this.lastVisibleLine = lastVisibleLine;

		return this;
	}

	public CoordinateHelper setHidpiScale(float hidpiScale) {
		this.hidpiScale = hidpiScale;

		return this;
	}

	/**
	 * @return how far through the current document the user is as a percentage (0-1)
	 */
	public float getPercentComplete() {
		return firstVisibleLine / (float)(imageHeight / pixelsPerLine - (lastVisibleLine - firstVisibleLine));
	}

	private int getOffset() {
		// If the panel is 1:1 then just draw everything in the top left hand corner, otherwise we need to gracefully scroll.
		if(imageHeight > panelHeight * hidpiScale) {
			return (int) ((imageHeight - panelHeight * hidpiScale) * getPercentComplete());
		} else {
			return 0;
		}
	}

	public Rectangle getImageSource() {
		int offset = getOffset();
        int end = (int) Math.min(offset + panelHeight * hidpiScale, imageHeight);
        srcHeight = end - offset;
		return new Rectangle(0, offset, panelWidth, end);
	}

    /**
     * Calculates the coordinates to draw the image onto within the frame. Make sure getImageSource has been called first!
     */
	public Rectangle getImageDestination() {
		return new Rectangle(0, 0, panelWidth, Math.min(srcHeight, panelHeight));
	}

	public Rectangle getViewport() {
		int offset = getOffset();
		return new Rectangle(
			0,
			(int)((firstVisibleLine * pixelsPerLine - offset) / hidpiScale),
			panelWidth - 1,
			(int)((lastVisibleLine - firstVisibleLine) * pixelsPerLine / hidpiScale)
		);
	}

	public LogicalPosition getPositionFor(int x, int y, boolean dragged) {
		if(x < 0) x = 0;
		if(y < 0) y = 0;
		if(x > panelWidth) x = panelWidth;
		if(y > panelHeight) y = panelHeight;

		// If the panel is 1:1 or has not been generated yet then mapping straight to the line that was selected is a good way to go.
		if(imageHeight < panelHeight) {
			return new LogicalPosition((int) (y / pixelsPerLine * hidpiScale), x);
		} else {
			if (dragged) {
				// When dragging use a percentage based position, 50% on window = 50% on document
				return new LogicalPosition((int) (y / (float)panelHeight * imageHeight) / pixelsPerLine, x);
			} else {
				int offsetLines = getOffset() / pixelsPerLine;
				// But for clicks we should take into account where the window currently is and adjust from there so
				// the user gets taken to the code block they clicked on.
				return new LogicalPosition( (int) (y / pixelsPerLine * hidpiScale) + offsetLines, x);
			}
		}
	}
}
