package com.clearwind.clearebook.factory;

import com.clearwind.clearebook.analysis.EBookAnalysis;
import com.clearwind.clearebook.analysis.impl.EpubAnalysis;
import com.clearwind.clearebook.analysis.impl.TxtAnalysis;

public class EBookAnalysisFactory {
    public static EBookAnalysis getAnalysis(String type) throws Exception {
        return switch (type.toUpperCase()) {
            case "EPUB" -> EpubAnalysis.getInstance();
            case "TXT" -> TxtAnalysis.getInstance();
            default -> throw new Exception("获取书籍类型解析器失败");
        };
    }
}
