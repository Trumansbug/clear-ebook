package com.clearwind.clearebook.window;

import com.clearwind.clearebook.analysis.EBookAnalysis;
import com.clearwind.clearebook.factory.EBookAnalysisFactory;
import com.clearwind.clearebook.setting.AppSettingsState;
import com.clearwind.clearebook.utils.FileUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

@Slf4j
public class MainToolWindowFactory implements ToolWindowFactory {
    
    private ToolWindow toolWindow;
    
    @Getter
    private static MainToolWindowFactory instance;

    public MainToolWindowFactory() {
        instance = this;
    }

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        this.toolWindow = toolWindow;
        MainToolWindowContent mainToolWindowContent = new MainToolWindowContent();
        Content content = ContentFactory.getInstance().createContent(mainToolWindowContent.getContentPanel(), "", false);
        toolWindow.getContentManager().addContent(content);
    }
    
    public void refresh() {
        MainToolWindowContent mainToolWindowContent = new MainToolWindowContent();
        Content content = ContentFactory.getInstance().createContent(mainToolWindowContent.getContentPanel(), "", false);
        this.toolWindow.getContentManager().removeAllContents(true);
        this.toolWindow.getContentManager().addContent(content);
    }

    private static class MainToolWindowContent {
        private final AppSettingsState settingsState = AppSettingsState.getInstance();
        private final JBPanel contentPanel = new JBPanel(new BorderLayout());
        private final JEditorPane editorPane;
        private final JComboBox<String> chapterComboBox;

        public MainToolWindowContent() {
            editorPane = new JEditorPane();
            editorPane.setContentType(settingsState.getContentType());
            editorPane.setEditable(false);
            editorPane.setFont(new Font(settingsState.getFontName(), Font.PLAIN, settingsState.getFontSize()));

            JBScrollPane scrollPane = new JBScrollPane(editorPane);

            // 章节下拉框
            chapterComboBox = new ComboBox<>();
            chapterComboBox.setPreferredSize(new Dimension(200, 30));

            // 上一章、下一章按钮
            JButton prevChapterButton = new JButton("上一章");
            JButton nextChapterButton = new JButton("下一章");

            // 按钮布局面板
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
            buttonPanel.add(chapterComboBox);
            buttonPanel.add(prevChapterButton);
            buttonPanel.add(nextChapterButton);

            // 底部按钮区域
            JPanel bottomPanel = new JPanel(new BorderLayout());
            bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            bottomPanel.add(buttonPanel, BorderLayout.CENTER);

            // 添加组件到主面板
            contentPanel.add(scrollPane, BorderLayout.CENTER);
            contentPanel.add(bottomPanel, BorderLayout.SOUTH);

            // 读取书籍
            settingsState.getChapter().keySet().forEach(chapterComboBox::addItem);

            // 绑定章节选择事件
            chapterComboBox.addActionListener(e -> loadChapterContent());

            // 绑定按钮点击事件
            prevChapterButton.addActionListener(e -> selectPreviousChapter());
            nextChapterButton.addActionListener(e -> selectNextChapter());

            // 默认加载记录的章节，如果记录没有就加载第一个章节内容
            if (chapterComboBox.getItemCount() > 0) {
                int index = 0;
                if (settingsState.getChapter() != null) {
                    index = settingsState.getChapter().getOrDefault(settingsState.getChapterName(), 0);
                }
                chapterComboBox.setSelectedIndex(index);
                loadChapterContent();
            }
        }

        private void loadChapterContent() {
            String selectedChapter = (String) chapterComboBox.getSelectedItem();
            if (selectedChapter == null) {
                editorPane.setText("无内容");
                return;
            }

            String content = getChapterContent(selectedChapter);
            editorPane.setText(content);
            editorPane.setCaretPosition(0);
            settingsState.setChapterName(selectedChapter);
        }

        private String getChapterContent(String chapterTitle) {
            try {
                EBookAnalysis eBookAnalysis = EBookAnalysisFactory.getAnalysis(FileUtil.getFileExtension(settingsState.getBookPath()));
                return eBookAnalysis.getChapterContent(chapterTitle);
            } catch (Exception e) {
                log.error("加载章节失败：{}", e.getMessage(), e);
                return "加载章节出错";
            }
        }

        private void selectPreviousChapter() {
            int selectedIndex = chapterComboBox.getSelectedIndex();
            if (selectedIndex > 0) {
                chapterComboBox.setSelectedIndex(selectedIndex - 1);
                loadChapterContent();
            }
        }

        private void selectNextChapter() {
            int selectedIndex = chapterComboBox.getSelectedIndex();
            if (selectedIndex < chapterComboBox.getItemCount() - 1) {
                chapterComboBox.setSelectedIndex(selectedIndex + 1);
                loadChapterContent();
            }
        }

        public JPanel getContentPanel() {
            return contentPanel;
        }
    }

}
