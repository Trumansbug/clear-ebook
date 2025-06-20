package com.clearwind.clearebook.analysis.impl;

import com.clearwind.clearebook.analysis.EBookAnalysis;
import com.clearwind.clearebook.setting.AppSettingsComponent;
import com.clearwind.clearebook.setting.AppSettingsState;
import com.google.common.io.Files;

import java.io.File;
import java.nio.charset.Charset;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TxtAnalysis implements EBookAnalysis {
    private static volatile TxtAnalysis instance;

    private TxtAnalysis() {
    }

    public static TxtAnalysis getInstance() {
        if (instance == null) {
            synchronized (TxtAnalysis.class) {
                if (instance == null) {
                    instance = new TxtAnalysis();
                }
            }
        }
        return instance;
    }
    
    @Override
    public void init(AppSettingsComponent settingsComponent) throws Exception {
        String filePath = settingsComponent.getBookPathText();
        String pattern = settingsComponent.getChapterPattern();

        List<String> lines = Files.readLines(new File(filePath), Charset.defaultCharset());

        Pattern chapterPattern = Pattern.compile(pattern);
        settingsComponent.getChapterSelect().removeAllItems();

        AppSettingsState settingsState = AppSettingsState.getInstance();
        settingsState.getChapter().clear();
        for (int i = 0, j = 0; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            Matcher matcher = chapterPattern.matcher(line);
            if (matcher.find()) {
                settingsComponent.getChapterSelect().addItem(line);
                settingsState.getChapter().put(line, j++);
            }
        }
    }

    @Override
    public String getChapterContent(String chapterTitle) throws Exception {
        String filePath = AppSettingsState.getInstance().getBookPath();
        String pattern = AppSettingsState.getInstance().getChapterPattern();

        List<String> lines = Files.readLines(new File(filePath), Charset.defaultCharset());

        Pattern chapterPattern = Pattern.compile(pattern);
        boolean found = false;
        StringBuilder content = new StringBuilder();

        for (String line : lines) {
            if (found) {
                // 如果遇到下一个章节，停止读取
                if (chapterPattern.matcher(line).find()) {
                    break;
                }
                content.append(line).append("\n");
            } else if (line.equals(chapterTitle)) {
                found = true;
            }
        }

        return content.toString();
    }
}
