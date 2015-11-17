package com.aqsara.tambalban;

/**
 * Created by dwi on 17/11/15.
 */
public class InfoItem {

    private int icon;
    private String title;

    public InfoItem(int icon, String title) {
        this.icon = icon;
        this.title = title;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}