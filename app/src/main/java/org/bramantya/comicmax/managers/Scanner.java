package org.bramantya.comicmax.managers;

import java.io.File;
import java.util.*;

import android.content.Context;
import android.os.AsyncTask;

import org.bramantya.comicmax.model.Storage;
import org.bramantya.comicmax.parsers.Parser;
import org.bramantya.comicmax.parsers.ParserFactory;
import org.bramantya.comicmax.model.Comic;

public class Scanner extends AsyncTask<Void, Void, Void> {
    private Storage mStorage;
    private Context mContext;
    private DirectoryIterator mDirIterator;

    public Scanner(Context context, Storage storage, File rootDir) {
        mContext = context;
        mStorage = storage;
        mDirIterator = new DirectoryIterator(rootDir);
    }

    @Override
    protected Void doInBackground(Void... params) {
        ArrayList<Comic> allComics = mStorage.listComics();
        HashMap<File, Comic> dirComics = new HashMap<>();
        for (Comic c : allComics) {
            dirComics.put(c.getFile(), c);
        }

        for (File file : mDirIterator) {
            if (isCancelled()) return null;

            if (dirComics.containsKey(file)) {
                dirComics.remove(file);
                continue;
            }

            Parser parser = ParserFactory.create(file);
            if (parser == null) continue;
            if (parser.numPages() > 0) {
                mStorage.addBook(file, parser.getType(), parser.numPages());
            }
        }

        for (Comic missing : dirComics.values()) {
            File coverCache = Utils.getCacheFile(mContext, missing.getFile().getAbsolutePath());
            coverCache.delete();
            mStorage.removeComic(missing.getId());
        }

        return null;
    }

    private class DirectoryIterator implements Iterable<File>, Iterator<File> {
        Deque<File> mFiles = new ArrayDeque<>();

        public DirectoryIterator(File root) {
            mFiles.push(root);
        }

        @Override
        public Iterator<File> iterator() {
            return this;
        }

        @Override
        public boolean hasNext() {
            return !mFiles.isEmpty();
        }

        @Override
        public File next() {
            File f = mFiles.pop();
            if (f.isDirectory()) {
                File[] files = f.listFiles();
                if (files != null) {
                    mFiles.addAll(Arrays.asList(files));
                }
            }
            return f;
        }

        @Override
        public void remove() {

        }
    }
}
