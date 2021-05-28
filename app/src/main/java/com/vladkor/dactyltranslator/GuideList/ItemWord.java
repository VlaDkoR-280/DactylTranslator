package com.vladkor.dactyltranslator.GuideList;

public class ItemWord {
    private String uri;
    private String name;

    public ItemWord(String name, String uri) {
        this.uri = uri;
        this.name = name;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
