package com.nkanaev.comics.parsers;

import java.io.InputStream;
import java.util.List;

public interface Parser2{
    public void close();
    public boolean isStreamResetable();
    public void clearCache();

    public boolean loadFile(String path);
    public List<String> getPageList();
    public InputStream getItemInputStream(String path);
    public boolean getLibraryData(String[] outVar);

    public String[] getMeta();
}//interface
