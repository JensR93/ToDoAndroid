package com.jens.ToDo.model;

public class Settings {
    private boolean showDescription = false;
    private boolean showExpiry = false;
    private boolean showBookmark = false;
    private boolean showDone=true;
    private boolean showContacts;


    public Settings(boolean showDescription, boolean showExpiry, boolean showBookmark, boolean showDone, boolean showContacts) {
        this.showDescription = showDescription;
        this.showExpiry = showExpiry;
        this.showBookmark = showBookmark;
        this.showDone = showDone;
        this.showContacts = showContacts;
    }

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

    public boolean isShowDone() {
        return showDone;
    }

    public void setShowDone(boolean showDone) {
        this.showDone = showDone;
    }

    public boolean isShowContacts() {
        return showContacts;
    }

    public void setShowContacts(boolean showContacts) {
        this.showContacts = showContacts;
    }
}
