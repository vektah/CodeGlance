package net.vektah.codeglance;

/**
 * Works out a weight for each character from 0-1, 1 being fairly opaque (lets say, like an M), 0 being totally transparent.
 */
public class CharacterWeight {
	public static float getWeight(char c) {
		// Whitespace and non printing characters are totally transparent.
		if(c <= 32) return 0;

		// Uppercase gets heaviest weighting
		if('A' <= c && c <= 'Z') return 1;

		// Numbers and lowercase all get slightly less
		if('a' <= c && c <= 'z') return 0.9f;
		if('0' <= c && c <= '9') return 0.9f;

		// Now we are down to some special cases.
		switch(c) {
			case '.': return 0.2f;
			case ',': return 0.25f;
			case '\'': return 0.2f;
			case '"': return 0.4f;
			case '-': return 0.3f;
			case '~': return 0.3f;
		}

		// Everything else is mediocre.
		return 0.5f;
	}
}
