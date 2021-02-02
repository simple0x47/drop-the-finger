/*
Code not removed, because it may provide to be useful for those looking for ways of implementing AdMob into a
LibGDX Game.

package com.elementalg.minigame


import android.content.Context
import com.google.android.gms.ads.*

/**
 * Implementation of AdMob compatible with LibGDX.
 *
 * @author Gabriel Amihalachioaie.
 */
class AdMobImplementation(private val androidLauncher: AndroidLauncher) : IAdsBridge {
    private lateinit var interstitialAd: InterstitialAd
    private lateinit var listener: IAdsListener

    fun onCreate(context: Context) {
        MobileAds.initialize(context)

        val configuration: RequestConfiguration = RequestConfiguration.Builder()
            .build()
        MobileAds.setRequestConfiguration(configuration)

        interstitialAd = InterstitialAd(context).apply {
            adUnitId = AD_UNIT_ID
            adListener = (
                    object : AdListener() {
                        override fun onAdClosed() {
                            super.onAdClosed()
                            load()

                            informListener()
                        }

                        override fun onAdFailedToLoad(p0: LoadAdError?) {
                            super.onAdFailedToLoad(p0)
                            informListener()
                        }
                    }
                    )
        }
    }

    override fun load() {
        androidLauncher.runOnUiThread {
            val adRequest: AdRequest = AdRequest.Builder().build()

            interstitialAd.loadAd(adRequest)
        }
    }

    override fun show(listener: IAdsListener) {
        this.listener = listener

        listener.runBeforeAd()

        androidLauncher.runOnUiThread {
            if (interstitialAd.isLoaded) {
                interstitialAd.show()
            } else {
                if (!interstitialAd.isLoading) {
                    load()
                }

                listener.runAfterAd()
            }
        }
    }

    fun informListener() {
        if (this::listener.isInitialized) {
            listener.runAfterAd()
        }
    }

    companion object {
        private const val AD_UNIT_ID = ""
    }
}*/