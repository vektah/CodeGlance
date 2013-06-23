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
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import com.intellij.ui.JBColor;
import com.intellij.util.ui.UIUtil;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created with IntelliJ IDEA.
 * User: adam
 * Date: 1/16/13
 * Time: 12:21 AM
 * To change this template use File | Settings | File Templates.
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
	            attribute_color = attribute.getDefaultAttributes().getForegroundColor();
	            if(attribute_color != null) color = attribute_color.getRGB();
            }

	        logger.warn(tokenType.toString());

	        for(int i = lexer.getTokenStart(); i < lexer.getTokenEnd(); i++) {
		        weight = CharacterWeight.getWeight(text.charAt(i));
		        if(weight == 0) continue;

		        charColor = mix(color, bgcolor, weight);

		        line = document.getLineNumber(i);
		        lineoffset = document.getLineStartOffset(line);

		        img.setRGB(i - lineoffset, line, charColor);
	        }

            lexer.advance();
        }
    }

	private int mix(int a, int b, float alpha) {
		int aR = a & 0xFF0000 >> 16;
		int aG = a & 0x00FF00 >> 8;
		int aB = a & 0x0000FF;

		int bR = b & 0xFF0000 >> 16;
		int bG = b & 0x00FF00 >> 8;
		int bB = b & 0x0000FF;

		int cR = (int) (aR * alpha + bR * (1 - alpha));
		int cB = (int) (aB * alpha + bB * (1 - alpha));
		int cG = (int) (aG * alpha + bG * (1 - alpha));

		return 0xFF000000 | (cR << 16) | (cB << 8) | cG;
	}

    public BufferedImage getImg() {
        return img;
    }
}
