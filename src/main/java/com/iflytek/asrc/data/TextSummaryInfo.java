package com.iflytek.asrc.data;


import java.util.List;

public class TextSummaryInfo {
    private String summary;
    private List<Highlight> highlights;

    public TextSummaryInfo(String summary, List<Highlight> highlights) {
        this.summary = summary;
        this.highlights = highlights;
    }

    // 添加getter和setter方法
    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public List<Highlight> getHighlights() {
        return highlights;
    }

    public void setHighlights(List<Highlight> highlights) {
        this.highlights = highlights;
    }
}
