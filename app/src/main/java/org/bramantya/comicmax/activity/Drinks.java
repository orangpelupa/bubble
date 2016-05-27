package org.bramantya.comicmax.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import org.bramantya.comicmax.R;
import org.bramantya.comicmax.util.IabHelper;
import org.bramantya.comicmax.util.IabResult;

import org.bramantya.comicmax.util.IabHelper;
import org.bramantya.comicmax.util.IabResult;
import org.bramantya.comicmax.util.Inventory;
import org.bramantya.comicmax.util.Purchase;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.view.View;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Shiki on 5/26/2016.
 */
public class Drinks extends Activity {

    private static final String TAG =
            "org.bramantya.comicmax";
    IabHelper mHelper;

    private Button clickButton;
    private Button buyButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_header);

        buyButton = (Button)findViewById(R.id.buyButton);
        clickButton = (Button)findViewById(R.id.clickButton);
        clickButton.setEnabled(false);

        String base64EncodedPublicKey =
                "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAkSufq2m/21FJDJBLE3x7YJ3b9wf5b4ODs2oOQBDIXp/YMTgRKKEh6VkrUnYdnKET+nA+NogtuVJ8gKgX9pCmSgolphE2iDjFr+xE94b/zmFT4SoustCI4xLxx9j1OEqIrTXbqIiS16um2wiij/z/CZIyBVRMTkWCLoDi4iHtuSNB+580akQKqB1H3inMsyKi/7zakgOvE5qvoU9RmpfrpKfGNb3lZbhlM6KLJhxYX8mWhF3acoIOLgncAFN0BG5wLRFTwlTZKqU68EIoubaVgonBgmc7Z8+lpssztIRkFeFHEfytAHbc2GNFUYmFSxWxWla9AEwz0ohJ3Nqb0U/8owIDAQAB";

        mHelper = new IabHelper(this, base64EncodedPublicKey);

        mHelper.startSetup(new
                                   IabHelper.OnIabSetupFinishedListener() {
                                       public void onIabSetupFinished(IabResult result)
                                       {
                                           if (!result.isSuccess()) {
                                               Log.d(TAG, "In-app Billing setup failed: " +
                                                       result);
                                           } else {
                                               Log.d(TAG, "In-app Billing is set up OK");
                                           }
                                       }
                                   });
    }

}
