package com.clearwind.clearebook.setting;

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.TextBrowseFolderListener;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;


@Slf4j
public class AppSettingsComponent {
    private final JPanel mainPanel;
    private final TextFieldWithBrowseButton bookPathText = new TextFieldWithBrowseButton();
    private final String[] SUPPORT_FILE_TYPE = {"epub", "txt"};

    @Getter
    private final ComboBox<String> chapterSelect = new ComboBox<>();

    @Getter
    private final ComboBox<String> fontNameSelect = new ComboBox<>();
    private final JBTextField fontSizeText = new JBTextField();
    private final JBTextField chapterPatternText = new JBTextField();


    public AppSettingsComponent() {
        bookPathText.addBrowseFolderListener(new TextBrowseFolderListener(new FileChooserDescriptor(true, false, false, false, false, false)
                .withFileFilter(f -> {
                    for (String type : SUPPORT_FILE_TYPE) {
                        if (f.getName().toLowerCase().endsWith("." + type)) {
                            return true;
                        }
                    }
                    return false;
                })));

        // 获取本地所有可用字体
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] fonts = ge.getAvailableFontFamilyNames();

        // 设置字体选项
        fontNameSelect.setModel(new DefaultComboBoxModel<>(fonts));

        mainPanel = FormBuilder.createFormBuilder()
                .addLabeledComponent(new JBLabel("Enter book path: "), bookPathText, 1, false)
                .addLabeledComponent(new JBLabel("Chapter select: "), chapterSelect, 1, false)
                .addLabeledComponent(new JBLabel("Font name: "), fontNameSelect, 1, false)
                .addLabeledComponent(new JBLabel("Font size: "), fontSizeText, 1, false)
                .addLabeledComponent(new JBLabel("Chapter Pattern (for TXT): "), chapterPatternText, 1, false)
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();
    }
    
    public void setChapter(Collection<String> chapters) {
        if (chapters == null || chapters.isEmpty()) {
            return;
        }
        
        chapters.forEach(chapterSelect::addItem);
    }

    public JPanel getPanel() {
        return mainPanel;
    }

    public JComponent getPreferredFocusedComponent() {
        return bookPathText;
    }

    @NotNull
    public String getBookPathText() {
        return bookPathText.getText();
    }

    public void setBookPathText(@NotNull String newText) {
        bookPathText.setText(newText);
    }

    public String getSelectedChapter() {
        return (String) chapterSelect.getSelectedItem();
    }

    public void setSelectedChapter(@NotNull String newText) {
        chapterSelect.setSelectedItem(newText);
    }

    @NotNull
    public String getFontName() {
        return (String) fontNameSelect.getSelectedItem();
    }

    public void setFontName(@NotNull String fontName) {
        fontNameSelect.setSelectedItem(fontName);
    }

    public Integer getFontSize() {
        return Integer.parseInt(fontSizeText.getText());
    }

    public void setFontSize(Integer fontSize) {
        fontSizeText.setText(String.valueOf(fontSize));
    }

    @NotNull
    public String getChapterPattern() {
        return chapterPatternText.getText();
    }

    public void setChapterPattern(@NotNull String pattern) {
        chapterPatternText.setText(pattern);
    }
}