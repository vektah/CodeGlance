package net.vektah.codeglance;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.event.VisibleAreaEvent;
import com.intellij.openapi.editor.event.VisibleAreaListener;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.TextEditor;
import com.intellij.openapi.project.Project;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created with IntelliJ IDEA.
 * User: adam
 * Date: 1/15/13
 * Time: 11:59 PM
 * To change this template use File | Settings | File Templates.
 */
public class GlancePanel extends JPanel implements VisibleAreaListener {
    private Project project;
    private Editor editor;
    GlanceFileRender render;
    private Logger logger = Logger.getInstance(getClass());

    public GlancePanel(Project project, FileEditor fileEditor) {
        this.project = project;
        this.editor = ((TextEditor) fileEditor).getEditor();

        editor.getScrollingModel().addVisibleAreaListener(this);

        this.setMinimumSize(new Dimension(50, 0));
        this.setSize(new Dimension(50, 0));
        setPreferredSize(new Dimension(200, 200));
        render = new GlanceFileRender(project, editor);
    }

    @Override
    public void revalidate() {
        super.revalidate();
    }

    @Override
    public void paint(Graphics g) {
        g.setColor(editor.getColorsScheme().getDefaultBackground());
        g.fillRect(0, 0, getWidth(), getHeight());

        BufferedImage img = render.getImg();

        // Draw the image and scale it to stretch vertically.
        g.drawImage(img,                                                    // source image
                0, 0, img.getWidth(), img.getHeight(),                          // destination location
                0, 0, img.getWidth(), editor.getDocument().getLineCount(),  // source location
                null);                                                      // observer

        // Draw the editor visible area
        Rectangle visible = render.getRenderAreaInChars();
        g.setColor(Color.GRAY);

        int width = visible.x + visible.width - visible.x;
        int height = visible.y + visible.height - visible.y;
        g.drawRect(visible.x, visible.y, width, height);
    }

    @Override
    public void visibleAreaChanged(VisibleAreaEvent visibleAreaEvent) {
        this.setPreferredSize(new Dimension(render.getRenderAreaInChars().width, 0));
        this.revalidate();
        this.repaint();
    }
}
