package com.clearwind.clearebook.analysis;

import com.clearwind.clearebook.setting.AppSettingsComponent;

public interface EBookAnalysis {
    void init(AppSettingsComponent settingsComponent) throws Exception;

    String getChapterContent(String chapterTitle) throws Exception;

    default String getContentType() {
        return "text/plain";
    }
}
