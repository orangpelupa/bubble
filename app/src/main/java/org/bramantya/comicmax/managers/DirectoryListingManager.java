package org.bramantya.comicmax.managers;

import android.text.TextUtils;
import org.bramantya.comicmax.model.Comic;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DirectoryListingManager {
    private final List<Comic> mComics;
    private final List<String> mDirectoryDisplays;
    private final File mLibraryDir;

    public DirectoryListingManager(List<Comic> comics, String libraryDir) {
        Collections.sort(comics, new Comparator<Comic>() {
            @Override
            public int compare(Comic lhs, Comic rhs) {
                return lhs.getFile().getParentFile().getName()
                        .compareTo(rhs.getFile().getParentFile().getAbsolutePath());
            }
        });
        mComics = comics;
        mLibraryDir = new File(libraryDir != null ? libraryDir : "/");

        List<String> directoryDisplays = new ArrayList<>();
        for (Comic comic : mComics) {
            File comicDir = comic.getFile().getParentFile();
            if (comicDir.equals(mLibraryDir)) {
                directoryDisplays.add("~ (" + comicDir.getName() + ")");
            }
            else if (comicDir.getParentFile().equals(mLibraryDir)) {
                directoryDisplays.add(comicDir.getName());
            }
            else {
                List<String> intermediateDirs = new ArrayList<>();
                File current = comicDir;
                while (!current.equals(mLibraryDir)) {
                    intermediateDirs.add(0, current.getName());
                    current = current.getParentFile();
                }
                directoryDisplays.add(TextUtils.join(" | ", intermediateDirs));
            }
        }

        mDirectoryDisplays = directoryDisplays;
    }

    public String getDirectoryDisplayAtIndex(int idx) {
        return mDirectoryDisplays.get(idx);
    }

    public Comic getComicAtIndex(int idx) {
        return mComics.get(idx);
    }

    public String getDirectoryAtIndex(int idx) {
        return mComics.get(idx).getFile().getParent();
    }

    public int getCount() {
        return mComics.size();
    }
}
