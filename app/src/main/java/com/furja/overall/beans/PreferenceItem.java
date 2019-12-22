package com.furja.overall.beans;

public class PreferenceItem {
    private String title;
    private String summary;

    public PreferenceItem(String title) {
        this.title = title;
    }

    public PreferenceItem() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }
}
