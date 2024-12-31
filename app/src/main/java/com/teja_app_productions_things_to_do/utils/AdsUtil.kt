package com.teja_app_productions_things_to_do.utils

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

object AdsUtil {

    const val MAP_AD_UNIT_ID = "ca-app-pub-1137313911154820/9217384238"
    const val SETTINGS_AD_UNIT_ID = "ca-app-pub-1137313911154820/2312734968"

    const val SETTINGS_TEST_AD_UNIT_ID = "ca-app-pub-3940256099942544/9214589741"
    const val MAP_TEST_AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712"
    const val TEST_DEVICE_HASHED_ID = "37A93361FA4402FA38C27C8FDA9C5337"

    var mapInterstitialAd: InterstitialAd? = null

    fun loadMapInterstitial(context: Context) {
        InterstitialAd.load(
            context,
            MAP_AD_UNIT_ID,
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    mapInterstitialAd = null
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    mapInterstitialAd = interstitialAd
                }
            }
        )
    }

    fun showMapInterstitial(activity: Activity, onAdDismissed: () -> Unit) {
        if (mapInterstitialAd != null) {
            mapInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdFailedToShowFullScreenContent(e: AdError) {
                    mapInterstitialAd = null
                }

                override fun onAdDismissedFullScreenContent() {
                    mapInterstitialAd = null

                    loadMapInterstitial(activity)
                    onAdDismissed()
                }
            }
            mapInterstitialAd?.show(activity)
        }
    }

    fun removeMapInterstitial() {
        mapInterstitialAd?.fullScreenContentCallback = null
        mapInterstitialAd = null
    }
}