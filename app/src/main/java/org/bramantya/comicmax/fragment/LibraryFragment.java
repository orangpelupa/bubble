package org.bramantya.comicmax.fragment;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.TypedValue;
import android.view.*;
import android.widget.*;
import org.bramantya.comicmax.Constants;
import org.bramantya.comicmax.R;
import org.bramantya.comicmax.activity.MainActivity;
import org.bramantya.comicmax.managers.DirectoryListingManager;
import org.bramantya.comicmax.managers.LocalCoverHandler;
import org.bramantya.comicmax.managers.Scanner;
import org.bramantya.comicmax.managers.Utils;
import org.bramantya.comicmax.model.Comic;
import org.bramantya.comicmax.model.Storage;
import org.bramantya.comicmax.view.DirectorySelectDialog;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;


public class LibraryFragment extends Fragment
        implements
        DirectorySelectDialog.OnDirectorySelectListener,
        AdapterView.OnItemClickListener,
        SwipeRefreshLayout.OnRefreshListener {
    private final static String BUNDLE_DIRECTORY_DIALOG_SHOWN = "BUNDLE_DIRECTORY_DIALOG_SHOWN";

//    private ArrayList<Comic> mComics;
    private DirectoryListingManager mComicsListManager;
    private DirectorySelectDialog mDirectorySelectDialog;
    private SwipeRefreshLayout mRefreshLayout;
    private View mEmptyView;
    private GridView mGridView;
    private Storage mStorage;
    private Scanner mScanner;
    private Picasso mPicasso;
    private boolean mIsLoading;

    public LibraryFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mStorage = Storage.getStorage(getActivity());
        getComics();

        mDirectorySelectDialog = new DirectorySelectDialog(getActivity());
        mDirectorySelectDialog.setCurrentDirectory(Environment.getExternalStorageDirectory());
        mDirectorySelectDialog.setOnDirectorySelectListener(this);

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_library, container, false);

        mPicasso = ((MainActivity) getActivity()).getPicasso();

        mRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.fragmentLibraryLayout);
        mRefreshLayout.setColorSchemeColors(R.color.primary);
        mRefreshLayout.setOnRefreshListener(this);
        mRefreshLayout.setEnabled(true);

        mGridView = (GridView) view.findViewById(R.id.groupGridView);
        mGridView.setAdapter(new GroupBrowserAdapter());
        mGridView.setOnItemClickListener(this);

        mEmptyView = view.findViewById(R.id.library_empty);

        int deviceWidth = Utils.getDeviceWidth(getActivity());
        int columnWidth = getActivity().getResources().getInteger(R.integer.grid_group_column_width);
        int numColumns = Math.round((float) deviceWidth / columnWidth);
        mGridView.setNumColumns(numColumns);

        showEmptyMessage(mComicsListManager.getCount() == 0);
        getActivity().setTitle(R.string.menu_library);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.library, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menuLibrarySetDir) {
            if (mScanner != null && mScanner.getStatus() != AsyncTask.Status.FINISHED)
                mScanner.cancel(true);

            mDirectorySelectDialog.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(BUNDLE_DIRECTORY_DIALOG_SHOWN,
                (mDirectorySelectDialog != null) && mDirectorySelectDialog.isShowing());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDirectorySelect(File file) {
        showEmptyMessage(false);

        SharedPreferences preferences = getActivity()
                .getSharedPreferences(Constants.SETTINGS_NAME, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(Constants.SETTINGS_LIBRARY_DIR, file.getAbsolutePath());
        editor.apply();

        if (mScanner == null || mScanner.getStatus() == AsyncTask.Status.FINISHED) {
            mScanner = new Scanner(getActivity(), mStorage, file) {
                @Override
                protected void onPreExecute() {
                    setLoading(true);
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    setLoading(false);
                }
            };
            mScanner.execute();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String path = mComicsListManager.getDirectoryAtIndex(position);
        LibraryBrowserFragment fragment = LibraryBrowserFragment.create(path);
        ((MainActivity)getActivity()).pushFragment(fragment);
    }

    @Override
    public void onRefresh() {
        if (mScanner == null || mScanner.getStatus() == AsyncTask.Status.FINISHED) {
            String libraryDir = getLibraryDir();
            if (libraryDir == null)
                return;

            mScanner = new Scanner(getActivity(), mStorage, new File(libraryDir)) {
                @Override
                protected void onPreExecute() {
                    setLoading(true);
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    setLoading(false);
                }
            };
            mScanner.execute();
        }
    }

    private void getComics() {
        List<Comic> comics = Storage.getStorage(getActivity()).listDirectoryComics();
        mComicsListManager = new DirectoryListingManager(comics, getLibraryDir());
    }

    private void setLoading(boolean isLoading) {
        mIsLoading = isLoading;

        if (isLoading) {
            mRefreshLayout.setRefreshing(true);
            mGridView.requestLayout();
        }
        else {
            mRefreshLayout.setRefreshing(false);
            getComics();
            showEmptyMessage(mComicsListManager.getCount() == 0);
            mGridView.requestLayout();
        }
    }

    private String getLibraryDir() {
        return getActivity()
                .getSharedPreferences(Constants.SETTINGS_NAME, 0)
                .getString(Constants.SETTINGS_LIBRARY_DIR, null);
    }

    private void showEmptyMessage(boolean show) {
        mRefreshLayout.setVisibility(!show ? View.VISIBLE : View.GONE);
        mEmptyView.setVisibility(show ? View.VISIBLE : View.GONE);

        if (!show) {
            // workaround: indicator does not show on view first appearance
            // https://code.google.com/p/android/issues/detail?id=77712
            mRefreshLayout.setProgressViewOffset(false, 0,
                    (int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP, 24,
                            getResources().getDisplayMetrics()));
        }
    }

    private final class GroupBrowserAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mIsLoading ? 0 : mComicsListManager.getCount();
        }

        @Override
        public Object getItem(int position) {
            return mComicsListManager.getComicAtIndex(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Comic comic = mComicsListManager.getComicAtIndex(position);
            String dirDisplay = mComicsListManager.getDirectoryDisplayAtIndex(position);

            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.card_group, parent, false);
            }

            ImageView groupImageView = (ImageView)convertView.findViewById(R.id.card_group_imageview);

            mPicasso.load(LocalCoverHandler.getComicCoverUri(comic))
                    .into(groupImageView);

            TextView tv = (TextView) convertView.findViewById(R.id.comic_group_folder);
            tv.setText(dirDisplay);

            return convertView;
        }
    }
}
