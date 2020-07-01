package com.jens.ToDo.model;

public class Settings {
    private boolean showDescription = false;
    private boolean showExpiry = false;
    private boolean showBookmark = false;

    public boolean isShowDescription() {
        return showDescription;
    }

    public void setShowDescription(boolean showDescription) {
        this.showDescription = showDescription;
    }

    public boolean isShowExpiry() {
        return showExpiry;
    }

    public void setShowExpiry(boolean showExpiry) {
        this.showExpiry = showExpiry;
    }

    public boolean isShowBookmark() {
        return showBookmark;
    }

    public void setShowBookmark(boolean showBookmark) {
        this.showBookmark = showBookmark;
    }

    public Settings(boolean showDescription, boolean showExpiry, boolean showBookmark) {
        this.showDescription = showDescription;
        this.showExpiry = showExpiry;
        this.showBookmark = showBookmark;
    }
}
