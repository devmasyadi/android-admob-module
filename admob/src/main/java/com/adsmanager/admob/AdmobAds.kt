package com.adsmanager.admob

import android.annotation.SuppressLint
import android.app.Activity
import android.util.DisplayMetrics
import android.view.View
import android.widget.*
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback


class AdmobAds : IAds {

    private var mInterstitialAd: InterstitialAd? = null
    private var mRewardedAd: RewardedAd? = null

    override fun initialize(
        activity: Activity,
        iInitialize: IInitialize,
        testDevices: List<String>?
    ) {
        MobileAds.initialize(activity) {
            MobileAds.setRequestConfiguration(
                RequestConfiguration.Builder()
                    .setTestDeviceIds(testDevices)
                    .build()
            )
            iInitialize.onInitializationComplete()
        }
    }

    override fun showBanner(
        activity: Activity,
        bannerView: RelativeLayout,
        adUnitId: String,
        callbackAds: CallbackAds
    ) {
        val adView = AdView(activity)
        adView.setAdSize(adSize(activity, bannerView))
        adView.adUnitId = adUnitId
        bannerView.removeAllViews()
        bannerView.addView(adView)
        adView.adListener = object : AdListener() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                // Code to be executed when an ad request fails.
                callbackAds.onAdFailedToLoad(adError.message)
            }

            override fun onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                callbackAds.onAdLoaded()
            }
        }
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
    }

    override fun loadInterstitial(activity: Activity, adUnitId: String) {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(activity, adUnitId, adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                mInterstitialAd = null
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                mInterstitialAd = interstitialAd
            }
        })
    }

    override fun showInterstitial(activity: Activity, adUnitId: String, callbackAds: CallbackAds) {
        if (mInterstitialAd != null) {
            mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    mInterstitialAd = null
                    loadInterstitial(activity, adUnitId)
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    mInterstitialAd = null
                }

                override fun onAdShowedFullScreenContent() {

                }
            }
            mInterstitialAd?.show(activity)
        } else {
            loadInterstitial(activity, adUnitId)
            callbackAds.onAdFailedToLoad("The interstitial ad wasn't ready yet.")
        }

    }

    @SuppressLint("InflateParams")
    override fun showNativeAds(
        activity: Activity,
        nativeView: RelativeLayout,
        sizeNative: SizeNative?,
        adUnitId: String,
        callbackAds: CallbackAds
    ) {

        val adLoader = AdLoader.Builder(activity, adUnitId)
            .forNativeAd { ad: NativeAd ->
                // Show the ad.
                val layoutNative = when (sizeNative) {
                    SizeNative.SMALL -> R.layout.admob_small_native
                    SizeNative.MEDIUM -> R.layout.admob_big_native
                    else -> R.layout.admob_small_native
                }
                val adView = activity.layoutInflater.inflate(layoutNative, null) as NativeAdView
                populateNativeAdView(ad, adView)
                nativeView.removeAllViews()
                nativeView.addView(adView)
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    // Handle the failure by logging, altering the UI, and so on.
                    callbackAds.onAdFailedToLoad(adError.message)
                }

                override fun onAdLoaded() {
                    super.onAdLoaded()
                    callbackAds.onAdLoaded()
                }
            })
            .withNativeAdOptions(
                NativeAdOptions.Builder()
                    // Methods in the NativeAdOptions.Builder class can be
                    // used here to specify individual options settings.
                    .build()
            )
            .build()

        adLoader.loadAd(AdRequest.Builder().build())
    }

    override fun loadRewards(activity: Activity, adUnitId: String) {
        val adRequest = AdRequest.Builder().build()
        RewardedAd.load(activity, adUnitId, adRequest, object : RewardedAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                mRewardedAd = null
            }

            override fun onAdLoaded(rewardedAd: RewardedAd) {
                mRewardedAd = rewardedAd
            }
        })
    }

    override fun showRewards(
        activity: Activity,
        adUnitId: String,
        callbackAds: CallbackAds,
        iRewards: IRewards?
    ) {
        if (mRewardedAd != null) {
            mRewardedAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    // Called when ad is dismissed.
                    // Set the ad reference to null so you don't show the ad a second time.
                    mRewardedAd = null
                    loadRewards(activity, adUnitId)
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    // Called when ad fails to show.
                    mRewardedAd = null
                }
            }
            mRewardedAd?.show(activity) {
                iRewards?.onUserEarnedReward(RewardsItem(it.amount, it.type))
            }
        } else {
            callbackAds.onAdFailedToLoad("The rewarded ad wasn't ready yet.")
            loadRewards(activity, adUnitId)
        }
    }

    private fun adSize(activity: Activity, bannerView: RelativeLayout): AdSize {
        val display = activity.windowManager.defaultDisplay
        val outMetrics = DisplayMetrics()
        display.getMetrics(outMetrics)

        val density = outMetrics.density

        var adWidthPixels = bannerView.width.toFloat()
        if (adWidthPixels == 0f) {
            adWidthPixels = outMetrics.widthPixels.toFloat()
        }

        val adWidth = (adWidthPixels / density).toInt()
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, adWidth)
    }

    private fun populateNativeAdView(nativeAd: NativeAd, adView: NativeAdView) {
        adView.mediaView = adView.findViewById<View>(R.id.ad_media) as MediaView
        adView.headlineView = adView.findViewById(R.id.ad_headline)
        adView.bodyView = adView.findViewById(R.id.ad_body)
        adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)
        adView.iconView = adView.findViewById(R.id.ad_app_icon)
        adView.priceView = adView.findViewById(R.id.ad_price)
        adView.starRatingView = adView.findViewById(R.id.ad_stars)
        adView.storeView = adView.findViewById(R.id.ad_store)
        adView.advertiserView = adView.findViewById(R.id.ad_advertiser)
        (adView.headlineView as TextView).text = nativeAd.headline
        nativeAd.mediaContent?.let { adView.mediaView?.setMediaContent(it) }
        if (nativeAd.body == null) {
            adView.bodyView?.visibility = View.INVISIBLE
        } else {
            adView.bodyView?.visibility = View.VISIBLE
            (adView.bodyView as TextView).text = nativeAd.body
        }
        if (nativeAd.callToAction == null) {
            adView.callToActionView?.visibility = View.INVISIBLE
        } else {
            adView.callToActionView?.visibility = View.VISIBLE
            (adView.callToActionView as Button).text = nativeAd.callToAction
        }
        if (nativeAd.icon == null) {
            adView.iconView?.visibility = View.GONE
        } else {
            (adView.iconView as ImageView).setImageDrawable(
                nativeAd.icon?.drawable
            )
            adView.iconView?.visibility = View.VISIBLE
        }
        if (nativeAd.price == null) {
            adView.priceView?.visibility = View.INVISIBLE
        } else {
            adView.priceView?.visibility = View.VISIBLE
            (adView.priceView as TextView).text = nativeAd.price
        }
        if (nativeAd.store == null) {
            adView.storeView?.visibility = View.INVISIBLE
        } else {
            adView.storeView?.visibility = View.VISIBLE
            (adView.storeView as TextView).text = nativeAd.store
        }
        if (nativeAd.starRating == null) {
            adView.starRatingView?.visibility = View.INVISIBLE
        } else {
            (adView.starRatingView as RatingBar).rating = nativeAd.starRating?.toFloat() ?: 5f
            adView.starRatingView?.visibility = View.VISIBLE
        }
        if (nativeAd.advertiser == null) {
            adView.advertiserView?.visibility = View.GONE
        } else {
            (adView.advertiserView as TextView).text = nativeAd.advertiser
            adView.advertiserView?.visibility = View.GONE
        }
        adView.setNativeAd(nativeAd)
    }


}