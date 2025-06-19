package com.clearwind.clearebook.setting;

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.TextBrowseFolderListener;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import lombok.extern.slf4j.Slf4j;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.TOCReference;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.List;

/**
 * 设置页面
 */
@Slf4j
public class AppSettingsConfigurable implements Configurable {
    private AppSettingsComponent mySettingsComponent;

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "Clear Ebook Setting";
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return mySettingsComponent.getPreferredFocusedComponent();
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        mySettingsComponent = new AppSettingsComponent();
        return mySettingsComponent.getPanel();
    }

    @Override
    public boolean isModified() {
        AppSettingsState settings = AppSettingsState.getInstance();
        if (StringUtils.isNotEmpty(mySettingsComponent.getBookPathText())) {
            boolean modified = !mySettingsComponent.getBookPathText().equals(settings.getBookPath());
            modified |= !settings.getChapterName().equals(mySettingsComponent.getChapter());
            modified |= !settings.getFontName().equals(mySettingsComponent.getFontName());
            modified |= settings.getFontSize() != mySettingsComponent.getFontSize();
            
            if (StringUtils.isNotBlank(mySettingsComponent.getBookPathText()) && !mySettingsComponent.getBookPathText().equals(settings.getBookPath())) {
                mySettingsComponent.chapterSelect.removeAllItems();
                try {
                    Book book = AppSettingsState.getBook(mySettingsComponent.getBookPathText());
                    List<TOCReference> tocReferences = book.getTableOfContents().getTocReferences();
                    settings.getChapter().clear();
                    for (int i = 0; i < tocReferences.size(); i++) {
                        TOCReference each = tocReferences.get(i);
                        mySettingsComponent.chapterSelect.addItem(each.getTitle());
                        settings.getChapter().put(each.getTitle(), i);
                    }
                } catch (IOException e) {
                    log.error("初始化书籍失败：{}", e.getMessage(), e);
                }
            }
            
            return modified;
        }
        
        return false;
    }

    @Override
    public void apply() {
        AppSettingsState settings = AppSettingsState.getInstance();
        settings.setBookPath(mySettingsComponent.getBookPathText());
        settings.setChapterName(mySettingsComponent.getChapter());
        settings.setFontName(mySettingsComponent.getFontName());
        settings.setFontSize(mySettingsComponent.getFontSize());
        settings.init();
    }

    @Override
    public void reset() {
        AppSettingsState settings = AppSettingsState.getInstance();
        mySettingsComponent.setBookPathText(settings.getBookPath());
        mySettingsComponent.setChapter(settings.getChapterName());
        mySettingsComponent.setFontName(settings.getFontName());
        mySettingsComponent.setFontSize(settings.getFontSize());
    }

    @Override
    public void disposeUIResources() {
        mySettingsComponent = null;
    }

    /**
     * 设置界面UI组件
     */
    static class AppSettingsComponent {
        private final JPanel mainPanel;
        private final TextFieldWithBrowseButton bookPathText = new TextFieldWithBrowseButton();
        private final ComboBox<String> chapterSelect = new ComboBox<>();
        private final ComboBox<String> fontNameSelect = new ComboBox<>();
        private final JBTextField fontSizeText = new JBTextField();

        public AppSettingsComponent() {
            bookPathText.addBrowseFolderListener(new TextBrowseFolderListener(new FileChooserDescriptor(true, false, false, false, false, false)
                    .withFileFilter(f -> f.getName().toLowerCase().endsWith(".epub"))));

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
                    .addComponentFillVertically(new JPanel(), 0)
                    .getPanel();
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
            chapterSelect.removeAllItems();
            if (StringUtils.isBlank(newText)) return;
            try {
                Book book = AppSettingsState.getBook(newText);
                book.getTableOfContents().getTocReferences().forEach(each -> chapterSelect.addItem(each.getTitle()));
            } catch (IOException e) {
                log.error("设置书籍路径失败：{}", e.getMessage(), e);
            }
        }

        public String getChapter() {
            return (String) chapterSelect.getSelectedItem();
        }

        public void setChapter(@NotNull String newText) {
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
    }
}

