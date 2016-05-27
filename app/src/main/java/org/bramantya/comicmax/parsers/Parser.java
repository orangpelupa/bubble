package org.bramantya.comicmax.parsers;

import java.io.IOException;
import java.io.InputStream;
import java.io.File;
import java.util.List;


public interface Parser {
    List<String> parse(File file) throws IOException;
    void destroy() throws IOException;

    String getType();
    InputStream getPage(int num) throws IOException;
    int numPages();
}
