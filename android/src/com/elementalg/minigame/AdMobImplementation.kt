package com.elementalg.minigame

import android.content.Context
import com.google.android.gms.ads.*

class AdMobImplementation(private val androidLauncher: AndroidLauncher) : IAdsBridge {
    private lateinit var interstitialAd: InterstitialAd
    private lateinit var listener: IAdsListener

    fun onCreate(context: Context) {
        MobileAds.initialize(context)

        val configuration: RequestConfiguration = RequestConfiguration.Builder()
                .setTestDeviceIds(listOf("ABCDEF012345"))
                .build()
        MobileAds.setRequestConfiguration(configuration)

        interstitialAd = InterstitialAd(context).apply {
            adUnitId = AD_UNIT_ID
            adListener = (
                object : AdListener() {
                    override fun onAdClosed() {
                        super.onAdClosed()
                        informListener()
                        load()
                    }

                    override fun onAdFailedToLoad(p0: LoadAdError?) {
                        super.onAdFailedToLoad(p0)
                        informListener()
                    }

                    override fun onAdLeftApplication() {
                        super.onAdLeftApplication()
                    }

                    override fun onAdOpened() {
                        super.onAdOpened()

                        beforeAd()
                    }

                    override fun onAdLoaded() {
                        super.onAdLoaded()
                    }

                    override fun onAdClicked() {
                        super.onAdClicked()
                    }

                    override fun onAdImpression() {
                        super.onAdImpression()
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

        androidLauncher.runOnUiThread {
            if (interstitialAd.isLoaded) {
                interstitialAd.show()
            }
            else {
                if (!interstitialAd.isLoading) {
                    load()
                }

                listener.runAfterAd()
            }
        }
    }

    fun beforeAd() {
        if (this::listener.isInitialized) {
            listener.runBeforeAd()
        }
    }

    fun informListener() {
        if (this::listener.isInitialized) {
            listener.runAfterAd()
        }
    }

    companion object {
        private const val AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712"
    }
}