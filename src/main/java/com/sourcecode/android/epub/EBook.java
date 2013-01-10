package com.sourcecode.android.epub;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Spine;
import nl.siegmann.epublib.domain.TOCReference;
import nl.siegmann.epublib.domain.TableOfContents;
import nl.siegmann.epublib.epub.EpubReader;

public class EBook {

    private String bookPath;
    // 章节信息
    private List<TOCReference> chapterlist;
    // 当前章节
    private TOCReference currentChapter;
    // 每页内容大小
    private Integer readPerSize;
    // 总页数
    private Integer totalPageSize;
    // 当前页
    private int currentPage;

    private List<ChapterEntry> chapterEntries;

    public String pageUp() {
        if (currentPage + 1 > totalPageSize) {
            return null;
        }

        return null;
    }

    public String pageDown() {
        if (currentPage - 1 < 0) {
            return null;
        }
        return null;
    }

    public static void readBook(Book e, int depth) throws IOException {
        TableOfContents tableOfContents = e.getTableOfContents();
        List<TOCReference> tocReferences = tableOfContents.getTocReferences();
        String tocHref = e.getNcxResource().getHref();
        System.out.println(tocHref);
        logTableOfContents(e.getTableOfContents().getTocReferences(), 0);

        // logContent(e, e.getSpine());
    }

    private static void logContent(Book e, Spine spine) throws IOException {
        for (int i = 4; i < 7; i++) {
            nl.siegmann.epublib.domain.Resource resource = spine.getResource(i);
            String href = resource.getHref();
            byte[] data = e.getResources().getByHref(href).getData();
            System.out.println(new String(data));
        }
    }

    private static void logTableOfContents(List<TOCReference> tocReferences, int depth) throws IOException {
        if (tocReferences == null) {
            return;
        }
        for (int i = 0; i < tocReferences.size() && i < 1; i++) {
            TOCReference tocReference = tocReferences.get(i);
            StringBuilder tocString = new StringBuilder();
            for (int j = 0; j < depth; j++) {
                tocString.append("\t");
            }
            byte[] data = tocReference.getResource().getData();
            System.out.println(new String(data, 0, 1400));
            tocString.append(tocReference.getTitle() + " " + tocReference.getCompleteHref());
            System.out.println(tocString.toString());

            logTableOfContents(tocReference.getChildren(), depth + 1);
        }
    }

    public static void main(String[] args) throws FileNotFoundException, IOException {
        File f = new File("1.epub");
        System.out.println(f.length());
        Book e = (new EpubReader()).readEpub(new FileInputStream(f));
        readBook(e, 0);
    }
}
