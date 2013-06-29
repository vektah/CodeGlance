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

import java.util.Random;

/**
 * Works out a weight for each character from 0-1, 1 being fairly opaque (lets say, like an M), 0 being totally transparent.
 */
public class CharacterWeight {
	private static Random random = new Random();

	public static float getWeight(char c) {
		// Whitespace and non printing characters are totally transparent.
		if(c <= 32) return 0;

		// Uppercase gets heaviest weighting
		if('A' <= c && c <= 'Z') return 0.6f + random.nextFloat() * 0.4f;

		// Numbers and lowercase all get slightly less
		if('a' <= c && c <= 'z') return 0.5f + random.nextFloat() * 0.4f;
		if('0' <= c && c <= '9') return 0.5f + random.nextFloat() * 0.4f;

		// Now we are down to some special cases.
		switch(c) {
			case '.': return 0.3f;
			case ',': return 0.35f;
			case '\'': return 0.4f;
		}

		// Everything else is mediocre.
		return 0.5f;
	}
}
