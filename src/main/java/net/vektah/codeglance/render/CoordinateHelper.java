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

import java.awt.*;


public class CoordinateHelper {
	private int panelHeight = 0;
	private int panelWidth = 0;
	private float hidpiScale = 1.0f;
    private int srcHeight = 0;
	private int pixelsPerLine = 2;
	private int imageHeight;
	private Minimap map;
	private double complete;

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

	public CoordinateHelper setMinimap(Minimap map) {
		this.map = map;
		this.imageHeight = map.height;

		return this;
	}

	public CoordinateHelper setHidpiScale(float hidpiScale) {
		this.hidpiScale = hidpiScale;

		return this;
	}

	public CoordinateHelper setPercentageComplete(double complete) {
		this.complete = complete;

		return this;
	}

	/**
	 * @return how far through the current document the user is as a percentage (0-1)
	 */
	public double getPercentComplete() {
		return complete;
	}

	public int getOffset() {
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

	public Rectangle getViewport(int firstVisibleLine, int lastVisibleLine) {
		return new Rectangle(
				0,
				(int)((firstVisibleLine * pixelsPerLine - getOffset()) / hidpiScale),
				panelWidth - 1,
				(int)((lastVisibleLine - firstVisibleLine) * pixelsPerLine / hidpiScale)
		);
	}

	/**
	 * Offset: The character offset from the start of file.
	 * LogicalPosition: This is the actual position within the document.
	 * ScreenSpace: Raw position the user can see. This is a scrolling window for long documents!
	 */
	public int screenSpaceToOffset(int y, boolean dragged) {
		if(y < 0) y = 0;
		if(y > panelHeight) y = panelHeight;
		int line;

		if (imageHeight < panelHeight) {
			// If the panel is short enough to fit on the screen then 1:1 is good.
			line = (int) (y / pixelsPerLine * hidpiScale);
		} else if (dragged) {
			// If we are dragging, then act like a conventional scroll bar.
			line = (int) (y / (float)panelHeight * imageHeight) / pixelsPerLine;
		} else {
			// Otherwise 1:1 with an offset so that clicks in long documents line up correctly.
			line = (int) ((y + getOffset()) / pixelsPerLine * hidpiScale);
		}

		if (map == null) return line * pixelsPerLine;

		return map.getOffsetForLine(line);
	}

	public int offsetToScreenSpace(int offset) {
		if (map == null) return offset / pixelsPerLine;

		int line = map.getLine(offset).number;

		if (imageHeight < panelHeight) {
			return (int) (line * pixelsPerLine * hidpiScale);
		} else {
			return (int) (line * pixelsPerLine * hidpiScale - getOffset());
		}
	}

	public int linesToPixels(int lines) {
		return (int) (lines * pixelsPerLine * hidpiScale);
	}

	public int pixelsToLines(int pixels) {
		if (imageHeight < panelHeight) {
			return (int) (pixels / pixelsPerLine * hidpiScale);
		} else {
			return (int) (pixels / pixelsPerLine / (panelHeight / (float)imageHeight) * hidpiScale);
		}
	}
}
