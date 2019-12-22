package com.furja.iqc.beans;

/**
 * RecyclerView的Item实体
 */

public class PartDetailItem {
    private String item_title;
    private String item_content;
    private boolean isChecked;
    public PartDetailItem()
    {

    }

    public PartDetailItem(String item_title, String item_content) {
        this.item_title = item_title;
        this.item_content = item_content;
    }

    public String getItem_title() {
        return item_title;
    }

    public void setItem_title(String item_title) {
        this.item_title = item_title;
    }

    public String getItem_content() {
        return item_content;
    }

    public void setItem_content(String item_content) {
        this.item_content = item_content;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
