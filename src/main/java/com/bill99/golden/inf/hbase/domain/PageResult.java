package com.bill99.golden.inf.hbase.domain;

import java.util.List;

public class PageResult {
    public static String VAR_PAGE_DATA = "PAGE_RESULT";
    private List<?> pageData;
    private int records;
    private int pages;

    public PageResult() {

    }
    public PageResult(List<?> pageData, int records, int pages) {
        this.setPageData(pageData);
        this.setRecords(records);
        this.setPages(pages);
    }

    public List<?> getPageData() {
        return pageData;
    }
    public void setPageData(List<?> pageData) {
        this.pageData = pageData;
    }
    public int getRecords() {
        return records;
    }
    public void setRecords(int records) {
        this.records = records;
    }
    public int getPages() {
        return pages;
    }
    public void setPages(int pages) {
        this.pages = pages;
    }

}
