package com.clearwind.clearebook.setting;

import com.clearwind.clearebook.window.MainToolWindowFactory;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.intellij.util.xmlb.annotations.Transient;
import lombok.Data;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.TOCReference;
import nl.siegmann.epublib.domain.TableOfContents;
import nl.siegmann.epublib.epub.EpubReader;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

@State(
        name = "com.clearwind.clearebook.setting.AppSettingsState",
        storages = @Storage("EbookReader.xml")
)
@Data
public class AppSettingsState implements PersistentStateComponent<AppSettingsState> {
    /**
     * 书籍路径
     */
    private String bookPath = "";
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

    public void init() {
        MainToolWindowFactory mainToolWindowFactory = MainToolWindowFactory.getInstance();
        mainToolWindowFactory.refresh();
    }

    public static Book getBook(String path) throws IOException {
        try (FileInputStream inputStream = new FileInputStream(path)) {
            EpubReader epubReader = new EpubReader();
            return epubReader.readEpub(inputStream);
        }
    }
}