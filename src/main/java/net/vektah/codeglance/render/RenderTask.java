package net.vektah.codeglance.render;

import com.intellij.openapi.editor.FoldRegion;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;

public class RenderTask implements Runnable {
	private Minimap minimap;
	private CharSequence text;
	private EditorColorsScheme cs;
	private SyntaxHighlighter hl;
	private final Runnable then;
	private FoldRegion[] folding;

	public RenderTask(Minimap minimap, CharSequence text, EditorColorsScheme cs, SyntaxHighlighter hl, FoldRegion[] folding, Runnable then) {
		this.minimap = minimap;
		this.text = text;
		this.cs = cs;
		this.hl = hl;
		this.then = then;
		this.folding = folding;
	}

	@Override public void run() {
		minimap.update(text, cs, hl, folding);
		then.run();
	}
}
