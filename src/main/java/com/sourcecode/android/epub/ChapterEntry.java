package com.sourcecode.android.epub;

import nl.siegmann.epublib.domain.Resource;

public class ChapterEntry {

    private String title;
    private Resource resource;
    private String href;
    private int size;

    public ChapterEntry() {
        super();
    }

    public ChapterEntry(String title, Resource resource, String href, int size) {
        super();
        this.title = title;
        this.resource = resource;
        this.href = href;
        this.size = size;
    }

}
