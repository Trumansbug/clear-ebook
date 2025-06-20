package com.clearwind.clearebook.analysis.impl;

import com.clearwind.clearebook.analysis.EBookAnalysis;
import com.clearwind.clearebook.setting.AppSettingsComponent;
import com.clearwind.clearebook.setting.AppSettingsState;
import lombok.extern.slf4j.Slf4j;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.TOCReference;
import nl.siegmann.epublib.epub.EpubReader;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;


@Slf4j
public class EpubAnalysis implements EBookAnalysis {
    private static volatile EBookAnalysis instance;

    private EpubAnalysis() {
    }

    public static EBookAnalysis getInstance() {
        if (instance == null) {
            synchronized (EpubAnalysis.class) {
                if (instance == null) {
                    instance = new EpubAnalysis();
                }
            }
        }
        return instance;
    }

    @Override
    public void init(AppSettingsComponent settingsComponent) throws Exception {
        AppSettingsState settings = AppSettingsState.getInstance();
        Book book = getBook(settingsComponent.getBookPathText());
        List<TOCReference> tocReferences = book.getTableOfContents().getTocReferences();
        settings.getChapter().clear();
        for (int i = 0; i < tocReferences.size(); i++) {
            TOCReference each = tocReferences.get(i);
            settingsComponent.getChapterSelect().addItem(each.getTitle());
            settings.getChapter().put(each.getTitle(), i);
        }
    }

    @Override
    public String getChapterContent(String chapterTitle) throws Exception {
        AppSettingsState settingsState = AppSettingsState.getInstance();
        Book book = getBook(settingsState.getBookPath());
        Optional<Resource> resourceOptional = book.getTableOfContents().getTocReferences().stream()
                .filter(each -> each.getTitle().equals(chapterTitle))
                .findFirst()
                .map(TOCReference::getResource);
        if (resourceOptional.isEmpty()) {
            return "加载章节失败";
        }

        String html = new String(resourceOptional.get().getData());
        Document doc = Jsoup.parse(html);
        Elements paragraphs = doc.select("p"); // 提取所有 <p> 段落

        StringBuilder contentBuilder = new StringBuilder();
        for (Element p : paragraphs) {
            contentBuilder.append("<p>").append(p.text()).append("</p>");
        }

        return contentBuilder.toString();
    }

    private static Book getBook(String path) throws IOException {
        try (FileInputStream inputStream = new FileInputStream(path)) {
            EpubReader epubReader = new EpubReader();
            return epubReader.readEpub(inputStream);
        }
    }

    @Override
    public String getContentType() {
        return "text/html";
    }
}
