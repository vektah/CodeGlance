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
	private int panelHeight;
	private int panelWidth;
	private int imageHeight;
	private int firstVisibleLine;
	private int lastVisibleLine;
	private float hidpiScale;
    private int srcHeight;
	public static final int PIXELS_PER_LINE = 2;

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
		return firstVisibleLine / (float)(imageHeight / PIXELS_PER_LINE - (lastVisibleLine - firstVisibleLine));
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
     * @return
     */
	public Rectangle getImageDestination() {
		return new Rectangle(0, 0, panelWidth, Math.min(srcHeight, panelHeight));
	}

	public Rectangle getViewport() {
		int offset = getOffset();
		return new Rectangle(
			0,
			(int)((firstVisibleLine * PIXELS_PER_LINE - offset) / hidpiScale),
			panelWidth - 1,
			(int)((lastVisibleLine - firstVisibleLine) * PIXELS_PER_LINE / hidpiScale)
		);
	}
}
