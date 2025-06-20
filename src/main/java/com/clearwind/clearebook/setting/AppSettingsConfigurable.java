package com.clearwind.clearebook.setting;

import com.clearwind.clearebook.analysis.EBookAnalysis;
import com.clearwind.clearebook.factory.EBookAnalysisFactory;
import com.clearwind.clearebook.utils.FileUtil;
import com.clearwind.clearebook.window.MainToolWindowFactory;
import com.intellij.openapi.options.Configurable;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

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
            modified |= !settings.getChapterName().equals(mySettingsComponent.getSelectedChapter());
            modified |= !settings.getFontName().equals(mySettingsComponent.getFontName());
            modified |= settings.getFontSize() != mySettingsComponent.getFontSize();
            modified |= settings.getChapterPattern().equals(mySettingsComponent.getChapterPattern());
            
            if (StringUtils.isNotBlank(mySettingsComponent.getBookPathText()) && !mySettingsComponent.getBookPathText().equals(settings.getBookPath())) {
                mySettingsComponent.getChapterSelect().removeAllItems();
                
                try {
                    EBookAnalysis eBookAnalysis = EBookAnalysisFactory.getAnalysis(FileUtil.getFileExtension(mySettingsComponent.getBookPathText()));
                    eBookAnalysis.init(mySettingsComponent);
                } catch (Exception e) {
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
        settings.setChapterName(mySettingsComponent.getSelectedChapter());
        settings.setFontName(mySettingsComponent.getFontName());
        settings.setFontSize(mySettingsComponent.getFontSize());
        settings.setChapterPattern(mySettingsComponent.getChapterPattern());
        settings.setFileType(FileUtil.getFileExtension(mySettingsComponent.getBookPathText()));
        
        try {
            EBookAnalysis eBookAnalysis = EBookAnalysisFactory.getAnalysis(settings.getFileType());
            String contentType = eBookAnalysis.getContentType();
            settings.setContentType(contentType);
        } catch (Exception e) {
            log.error("设置文件读取类型失败：{}", e.getMessage(), e);
        }

        MainToolWindowFactory.getInstance().refresh();
    }

    @Override
    public void reset() {
        AppSettingsState settings = AppSettingsState.getInstance();
        mySettingsComponent.setBookPathText(settings.getBookPath());
        mySettingsComponent.setChapter(settings.getChapter().keySet());
        mySettingsComponent.setSelectedChapter(settings.getChapterName());
        mySettingsComponent.setFontName(settings.getFontName());
        mySettingsComponent.setFontSize(settings.getFontSize());
        mySettingsComponent.setChapterPattern(settings.getChapterPattern());
    }

    @Override
    public void disposeUIResources() {
        mySettingsComponent = null;
    }
}

