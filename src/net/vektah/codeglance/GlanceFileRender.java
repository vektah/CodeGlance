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
    private Scale scale;
    private Logger logger = Logger.getInstance(getClass());

    public GlanceFileRender(Project project, Editor editor, Scale scale) {
        this.project = project;
        this.editor = editor;
        this.scale = scale;
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

        if (img == null || img.getWidth() < longest_line || img.getHeight() < line_count) {
            img = UIUtil.createImage(longest_line + 64, editor.getDocument().getLineCount() + 64, BufferedImage.TYPE_INT_ARGB);
            logger.info("Created new image");
        }

        Graphics g = img.getGraphics();
        int x1, x2, line, lineoffset;
        Document document = editor.getDocument();

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

            g.setColor(editor.getColorsScheme().getDefaultForeground());
            TextAttributesKey[] attributes = hl.getTokenHighlights(tokenType);
            for(TextAttributesKey attribute : attributes) {
                g.setColor(attribute.getDefaultAttributes().getForegroundColor());
            }

            line = document.getLineNumber(lexer.getTokenStart());
            lineoffset = document.getLineStartOffset(line);
            x1 = scale.charXToPointX(lexer.getTokenStart() - lineoffset);
            x2 = scale.charXToPointX(lexer.getTokenEnd() - lineoffset);

            g.drawLine(x1, line, x2, line);

            lexer.advance();
        }
    }

    public BufferedImage getImg() {
        return img;
    }
}
