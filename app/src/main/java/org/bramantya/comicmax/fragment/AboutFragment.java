package org.bramantya.comicmax.fragment;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.bramantya.comicmax.R;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class AboutFragment extends Fragment implements View.OnClickListener {

    private AdView mAdView;

    private class LibraryDescription {
        public final String name;
        public final String description;
        public final String license;
        public final String owner;
        public final String link;

        LibraryDescription(String name, String description, String license, String owner, String link) {
            this.name = name;
            this.description = description;
            this.license = license;
            this.owner = owner;
            this.link = link;
        }
    }

    private LibraryDescription[] mDescriptions = new LibraryDescription[]{
            new LibraryDescription(
                    "Bubble",
                    "The base of this app",
                    "Github",
                    "nkanaev",
                    "https://github.com/nkanaev/bubble"
            ),
            new LibraryDescription(
                    "Max Comic Viewer",
                    "Open source project",
                    "Github",
                    "orangpelupa",
                    "https://github.com/orangpelupa/bubble"
            ),
            new LibraryDescription(
                    "Picasso",
                    "A powerful image downloading and caching library for Android",
                    "Apache Version 2.0",
                    "Square",
                    "https://github.com/square/picasso"
            ),
            new LibraryDescription(
                    "Junrar",
                    "Plain java unrar util",
                    "Unrar License",
                    "Edmund Wagner",
                    "https://github.com/edmund-wagner/junrar"
            )
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_about, container, false);

        LinearLayout libsLayout = (LinearLayout) view.findViewById(R.id.about_libraries);

        ((TextView) view.findViewById(R.id.aboutVersion)).setText(getVersionString());

        mAdView = (AdView) view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mAdView.loadAd(adRequest);

        for (int i = 0; i < mDescriptions.length; i++) {
            View cardView = inflater.inflate(R.layout.card_deps, libsLayout, false);

            ((TextView) cardView.findViewById(R.id.libraryName)).setText(mDescriptions[i].name);
            ((TextView) cardView.findViewById(R.id.libraryCreator)).setText(mDescriptions[i].owner);
            ((TextView) cardView.findViewById(R.id.libraryDescription)).setText(mDescriptions[i].description);
            ((TextView) cardView.findViewById(R.id.libraryLicense)).setText(mDescriptions[i].license);

            cardView.setTag(mDescriptions[i].link);
            cardView.setOnClickListener(this);
            libsLayout.addView(cardView);
        }

        return view;
    }

    private String getVersionString() {
        try {
            PackageInfo pi = getActivity()
                    .getPackageManager()
                    .getPackageInfo(getActivity().getPackageName(), 0);
            return "Version " + pi.versionName + " (" + Integer.toString(pi.versionCode) + ")";
        }
        catch (PackageManager.NameNotFoundException e) {
            return "";
        }
    }

    @Override
    public void onClick(View v) {
        String link = (String) v.getTag();
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
        startActivity(browserIntent);
    }
}
