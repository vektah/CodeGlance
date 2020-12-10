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

package net.vektah.codeglance.config;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;

import javax.swing.*;
import java.awt.*;

@SuppressWarnings("unchecked")
public class ConfigForm {
    private JComboBox pixelsPerLine;
    private JPanel rootPanel;
    private JCheckBox disabled;
    private JComboBox jumpToPosition;
    private JComboBox clickStyle;
    private JTextField width;
    private JTextField viewportColor;
    private JTextField minLinesCount;
    private JTextField minWindowWidth;
    private JComboBox renderStyle;
    private JComboBox alignment;
    private JCheckBox locked;
    private JCheckBox bgColorEnabled;
    private JTextField bgColor;
    private JTextField dividerColor;
    private JCheckBox dividerColorEnabled;

    public ConfigForm() {
        pixelsPerLine.setModel(new DefaultComboBoxModel(new Integer[]{1, 2, 3, 4}));
        jumpToPosition.setModel(new DefaultComboBoxModel(new String[]{"Mouse Down", "Mouse Up"}));
        clickStyle.setModel(new DefaultComboBoxModel(new String[]{"Scrollbar (old sublime)", "To Text (new sublime)"}));
        renderStyle.setModel(new DefaultComboBoxModel(new String[]{"Clean", "Accurate"}));
        alignment.setModel(new DefaultComboBoxModel(new String[]{"Right", "Left"}));
    }

    public JPanel getRoot() {
        return rootPanel;
    }

    public int getPixelsPerLine() {
        return (Integer) pixelsPerLine.getSelectedItem();
    }

    public void setPixelsPerLine(int pixelsPerLine) {
        this.pixelsPerLine.setSelectedIndex(pixelsPerLine - 1);
    }

    public boolean isDisabled() {
        return disabled.getModel().isSelected();
    }

    public void setDisabled(boolean isDisabled) {
        disabled.getModel().setSelected(isDisabled);
    }

    public boolean isLocked() {
        return locked.getModel().isSelected();
    }

    public void setLocked(boolean isLocked) {
        locked.getModel().setSelected(isLocked);
    }

    public boolean jumpOnMouseDown() {
        return jumpToPosition.getSelectedIndex() == 0;
    }

    public void setJumpOnMouseDown(boolean jump) {
        jumpToPosition.setSelectedIndex(jump ? 0 : 1);
    }

    public boolean percentageBasedClick() {
        return clickStyle.getSelectedIndex() == 0;
    }

    public void setPercentageBasedClick(boolean click) {
        clickStyle.setSelectedIndex(click ? 0 : 1);
    }

    public String getViewportColor() {
        return viewportColor.getText();
    }

    public void setViewportColor(String color) {
        viewportColor.setText(color);
    }

    public String getBgColor() { return bgColor.getText();}

    public void setBgColor(String color) {bgColor.setText(color);}

    public boolean getBgColorEnabled(){
        return bgColorEnabled.getModel().isSelected();
    }

    public void setBgColorEnabled(boolean b) {
        bgColorEnabled.getModel().setSelected(b);
    }

    public String getDividerColor() { return dividerColor.getText();}

    public void setDividerColor(String color) {dividerColor.setText(color);}

    public boolean getDividerColorEnabled(){
        return dividerColorEnabled.getModel().isSelected();
    }

    public void setDividerColorEnabled(boolean b) {
        dividerColorEnabled.getModel().setSelected(b);
    }

    public boolean getCleanStyle() {
        return renderStyle.getSelectedIndex() == 0;
    }

    public void setCleanStyle(boolean isClean) {
        renderStyle.setSelectedIndex(isClean ? 0 : 1);
    }

    public boolean isRightAligned() {
        return alignment.getSelectedIndex() == 0;
    }

    public void setRightAligned(boolean isRightAligned) {
        renderStyle.setSelectedIndex(isRightAligned ? 0 : 1);
    }

    public int getWidth() {
        try {
            return Integer.parseInt(width.getText());
        } catch (NumberFormatException e) {
            return 100;
        }
    }

    public void setWidth(int width) {
        this.width.setText(Integer.toString(width));
    }

    public int getMinLinesCount() {
        try {
            return Integer.parseInt(minLinesCount.getText());
        } catch (NumberFormatException e) {
            return 85;
        }
    }

    public void setMinLinesCount(int minLinesCount) {
        this.minLinesCount.setText(Integer.toString(minLinesCount));
    }

    public int getMinWindowWidth() {
        try {
            return Integer.parseInt(minWindowWidth.getText());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public void setMinWindowWidth(int minWindowWidth) {
        this.minWindowWidth.setText(Integer.toString(minWindowWidth));
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        rootPanel = new JPanel();
        rootPanel.setLayout(new GridLayoutManager(11, 4, new Insets(0, 0, 0, 0), -1, -1));
        final JLabel label1 = new JLabel();
        label1.setText("Pixels Per Line:");
        label1.setDisplayedMnemonic('P');
        label1.setDisplayedMnemonicIndex(0);
        rootPanel.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        pixelsPerLine = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel1 = new DefaultComboBoxModel();
        defaultComboBoxModel1.addElement("1");
        defaultComboBoxModel1.addElement("2");
        defaultComboBoxModel1.addElement("3");
        defaultComboBoxModel1.addElement("4");
        pixelsPerLine.setModel(defaultComboBoxModel1);
        rootPanel.add(pixelsPerLine, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(200, 24), null, 0, false));
        final Spacer spacer1 = new Spacer();
        rootPanel.add(spacer1, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Jump to position on:");
        rootPanel.add(label2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        jumpToPosition = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel2 = new DefaultComboBoxModel();
        defaultComboBoxModel2.addElement("Mouse Down");
        defaultComboBoxModel2.addElement("Mouse Up");
        jumpToPosition.setModel(defaultComboBoxModel2);
        rootPanel.add(jumpToPosition, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Disabled:");
        rootPanel.add(label3, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        disabled = new JCheckBox();
        disabled.setText("");
        rootPanel.add(disabled, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("Click style");
        rootPanel.add(label4, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        clickStyle = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel3 = new DefaultComboBoxModel();
        defaultComboBoxModel3.addElement("Scrollbar (old sublime)");
        defaultComboBoxModel3.addElement("To Text (new sublime)");
        clickStyle.setModel(defaultComboBoxModel3);
        rootPanel.add(clickStyle, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        width = new JTextField();
        rootPanel.add(width, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label5 = new JLabel();
        label5.setText("Width:");
        rootPanel.add(label5, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label6 = new JLabel();
        label6.setText("Viewport Color");
        rootPanel.add(label6, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        viewportColor = new JTextField();
        rootPanel.add(viewportColor, new GridConstraints(5, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label7 = new JLabel();
        label7.setText("Minimum lines count:");
        label7.setToolTipText("Minimum number of lines to show minimap.");
        rootPanel.add(label7, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        rootPanel.add(spacer2, new GridConstraints(10, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        minLinesCount = new JTextField();
        minLinesCount.setText("");
        rootPanel.add(minLinesCount, new GridConstraints(6, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label8 = new JLabel();
        label8.setText("Render Style");
        rootPanel.add(label8, new GridConstraints(8, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        renderStyle = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel4 = new DefaultComboBoxModel();
        defaultComboBoxModel4.addElement("Clean");
        defaultComboBoxModel4.addElement("Accurate");
        renderStyle.setModel(defaultComboBoxModel4);
        rootPanel.add(renderStyle, new GridConstraints(8, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label9 = new JLabel();
        label9.setText("Alignment (restart)");
        rootPanel.add(label9, new GridConstraints(9, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        alignment = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel5 = new DefaultComboBoxModel();
        defaultComboBoxModel5.addElement("Right");
        defaultComboBoxModel5.addElement("Left");
        alignment.setModel(defaultComboBoxModel5);
        rootPanel.add(alignment, new GridConstraints(9, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label10 = new JLabel();
        label10.setText("Minimum Window Width:");
        rootPanel.add(label10, new GridConstraints(7, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        minWindowWidth = new JTextField();
        rootPanel.add(minWindowWidth, new GridConstraints(7, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        locked = new JCheckBox();
        locked.setText("lock");
        rootPanel.add(locked, new GridConstraints(4, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        label1.setLabelFor(pixelsPerLine);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return rootPanel;
    }
}
