package org.bramantya.comicmax.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
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
import android.widget.ImageButton;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

/**
 * Created by Shiki on 5/26/2016.
 */
public class InAppBillingActivity extends Activity {

    private static final String TAG = "com.example.inappbilling";
    IabHelper mHelper;
    static final String ITEM_SKU = "android.test.purchased";

    private ImageButton clickButton;
    private ImageButton buyWaterButton;

    private AdView mAdView;
    private Button btnFullscreenAd;

    public void buyWater(View view) {
        try {
            mHelper.launchPurchaseFlow(this, ITEM_SKU, 10001,
                    mPurchaseFinishedListener, "mypurchasetoken");
        } catch (IabHelper.IabAsyncInProgressException e) {
            e.printStackTrace();
        }
    }


    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener
            = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result,
                                          Purchase purchase)
        {
            if (result.isFailure()) {
                // Handle error
                return;
            }
            else if (purchase.getSku().equals(ITEM_SKU)) {
                consumeItem();
               /* buyButton.setEnabled(false); */
            }

        }
    };

    public void consumeItem() {
        try {
            mHelper.queryInventoryAsync(mReceivedInventoryListener);
        } catch (IabHelper.IabAsyncInProgressException e) {
            e.printStackTrace();
        }
    }

    IabHelper.QueryInventoryFinishedListener mReceivedInventoryListener
            = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result,
                                             Inventory inventory) {

            if (result.isFailure()) {
                // Handle failure
            } else {
                try {
                    mHelper.consumeAsync(inventory.getPurchase(ITEM_SKU),
                            mConsumeFinishedListener);
                } catch (IabHelper.IabAsyncInProgressException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener =
            new IabHelper.OnConsumeFinishedListener() {
                public void onConsumeFinished(Purchase purchase,
                                              IabResult result) {

                    if (result.isSuccess()) {
                        clickButton.setEnabled(true);
                    } else {
                        // handle error
                    }
                }
            };

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data)
    {
        if (!mHelper.handleActivityResult(requestCode,
                resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }




    @Override
    public void onDestroy() {

        if (mAdView != null) {
            mAdView.destroy();
        }

        super.onDestroy();
        if (mHelper != null) try {
            mHelper.dispose();
        } catch (IabHelper.IabAsyncInProgressException e) {
            e.printStackTrace();
        }
        mHelper = null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_app_billing);

        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mAdView.loadAd(adRequest);

       /* btnFullscreenAd = (Button) findViewById(R.id.btn_fullscreen_ad);
        btnFullscreenAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(InAppBillingActivity.this, SecondActivity.class));
            }
        }); */

        buyWaterButton = (ImageButton)findViewById(R.id.buyWaterButton);
        clickButton = (ImageButton)findViewById(R.id.clickButton);
       /* clickButton.setEnabled(false); */

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

    public void buttonClicked (View view)
    {
       /* clickButton.setEnabled(false); */
        buyWaterButton.setEnabled(true);
        Intent myIntent = new Intent(getApplicationContext(), SecondActivity.class);
        startActivityForResult(myIntent, 0);

    }

    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
    }



}