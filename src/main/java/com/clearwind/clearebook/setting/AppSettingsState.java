package com.clearwind.clearebook.setting;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

@State(
        name = "com.clearwind.clearebook.setting.AppSettingsState",
        storages = @Storage("ClearEBook.xml")
)
@Data
public class AppSettingsState implements PersistentStateComponent<AppSettingsState> {
    /**
     * 书籍路径
     */
    private String bookPath = "";
    /**
     * 书籍类型
     */
    private String fileType = "";
    /**
     * 
     */
    private String contentType = "text/plain";
    /**
     * 章节
     */
    private Map<String, Integer> chapter = new LinkedHashMap<>();
    /**
     * 章节
     */
    private String chapterName = "";
    /**
     * 字体
     */
    private String fontName = "Default";
    /**
     * 字体大小
     */
    private int fontSize = 14;
    /**
     * 章节匹配表达式
     */
    private String chapterPattern = "^第.*章";

    
    
    public static AppSettingsState getInstance() {
        return ApplicationManager.getApplication().getService(AppSettingsState.class);
    }

    @Override
    public @Nullable AppSettingsState getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull AppSettingsState state) {
        XmlSerializerUtil.copyBean(state, this);
    }
}