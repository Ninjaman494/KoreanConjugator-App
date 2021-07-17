package com.a494studios.koreanconjugator;

import android.view.View;

import androidx.test.core.app.ApplicationProvider;

import com.a494studios.koreanconjugator.utils.Logger;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;

public class TestCustomApplication extends CustomApplication {
    private String serverUrl = "testUrl";
    private boolean isAdFree;

    @Override
    public void onCreate() {
        FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext());
        Logger.initialize(FirebaseAnalytics.getInstance(this));
    }

    @Override
    public String getServerUrl() {
        return serverUrl;
    }

    @Override
    public void handleAdCard(AdView adView) {
        if (isAdFree) {
            adView.setVisibility(View.GONE);
        } else {
            adView.loadAd(new AdRequest.Builder().build());
        }
    }

    @Override
    public boolean isAdFree() {
        return isAdFree;
    }

    public void setServerUrl(String url) {
        this.serverUrl = url;
    }

    public void setAdFree(boolean isAdFree) {
        this.isAdFree = isAdFree;
    }
}
