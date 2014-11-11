package com.creativefunapps.tileschallenge;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * An {@link Activity} that requests and can display an InterstitialAd.
 */
public class Advertisement extends PortraitActivity {
    /** The log tag. */
    private static final String LOG_TAG = "Advertisement";

    /** Your ad unit id. Replace with your actual ad unit id. */
    private static final String AD_UNIT_ID = "ca-app-pub-9128082053165311/3395872781";

    /** The interstitial ad. */
    private InterstitialAd interstitialAd;

    /** The button that show the interstitial. */
    //private Button showButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advertisement);

        // Create an ad.
        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(AD_UNIT_ID);

        //Load the ad automatically when creating activity
        View v = null; //view not used
        loadInterstitial(v);

        // Set the AdListener.
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                Log.d(LOG_TAG, "onAdLoaded");
                Log.d(LOG_TAG, "Interstitial ad shown.");
                //show the ad
                interstitialAd.show();
                /*Toast.makeText(Advertisement.this, "onAdLoaded", Toast.LENGTH_SHORT).show();

                // Change the button text and enable the button.
                showButton.setText("Show Interstitial");
                showButton.setEnabled(true);*/
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                String message = String.format("onAdFailedToLoad (%s)", getErrorReason(errorCode));
                Log.d(LOG_TAG, "Interstitial ad was not ready to be shown.");
                Log.d(LOG_TAG, message);
                //close activity
                finish();
                /*Toast.makeText(Advertisement.this, message, Toast.LENGTH_SHORT).show();

                // Change the button text and disable the button.
                showButton.setText("Ad Failed to Load");
                showButton.setEnabled(false);*/
            }

            @Override
            public void onAdClosed() {
                finish();
                super.onAdClosed();
            }
        });

        /*showButton = (Button) findViewById(R.id.showButton);
        showButton.setEnabled(false);*/
    }

    /** Called when the Load Interstitial button is clicked. */
    public void loadInterstitial(View unusedView) {
        // Disable the show button until the new ad is loaded.
        /*showButton.setText("Loading Interstitial...");
        showButton.setEnabled(false);*/

        // Check the logcat output for your hashed device ID to get test ads on a physical device.
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("ABB18ACF9860B7B99B386733FDF7FF18")
                .build();

        // Load the interstitial ad.
        interstitialAd.loadAd(adRequest);
    }

    /** Called when the Show Interstitial button is clicked. */
    public void showInterstitial(View unusedView) {
        // Disable the show button until another interstitial is loaded.
        /*showButton.setText("Interstitial Not Ready");
        showButton.setEnabled(false);*/

        if (interstitialAd.isLoaded()) {
            interstitialAd.show();
        } else {
            Log.d(LOG_TAG, "Interstitial ad was not ready to be shown.");
        }
    }

    /** Gets a string error reason from an error code. */
    private String getErrorReason(int errorCode) {
        String errorReason = "";
        switch(errorCode) {
            case AdRequest.ERROR_CODE_INTERNAL_ERROR:
                errorReason = "Internal error";
                break;
            case AdRequest.ERROR_CODE_INVALID_REQUEST:
                errorReason = "Invalid request";
                break;
            case AdRequest.ERROR_CODE_NETWORK_ERROR:
                errorReason = "Network Error";
                break;
            case AdRequest.ERROR_CODE_NO_FILL:
                errorReason = "No fill";
                break;
        }
        return errorReason;
    }
}
