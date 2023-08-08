package com.adsmanager.admobmodule

import android.os.Bundle
import android.widget.Button
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import com.adsmanager.admob.AdmobAds
import com.adsmanager.admob.AdmobOpenAd
import com.adsmanager.core.CallbackAds
import com.adsmanager.core.CallbackOpenAd
import com.adsmanager.core.SizeBanner
import com.adsmanager.core.SizeNative
import com.adsmanager.core.iadsmanager.IInitialize
import com.adsmanager.core.rewards.IRewards
import com.adsmanager.core.rewards.RewardsItem

class MainActivity : AppCompatActivity() {

    private lateinit var admobAds: AdmobAds
    private val bannerId = "ca-app-pub-3940256099942544/6300978111"
    private val interstitialId = "ca-app-pub-3940256099942544/1033173712"
    private val nativeId = "ca-app-pub-9687688364831872/5063874431"
    private val rewardsId = "ca-app-pub-3940256099942544/5224354917"
    private val appOpenId = "ca-app-pub-3940256099942544/3419835294"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ConfigAds.adUnitOpenId = appOpenId
        val appOpenAdManager = AdmobOpenAd()

        admobAds = AdmobAds()
        admobAds.initialize(this, null, object : IInitialize {
            override fun onInitializationComplete() {
                appOpenAdManager.loadAd(this@MainActivity, appOpenId)
                admobAds.loadGdpr(this@MainActivity, true)
                admobAds.loadInterstitial(this@MainActivity, interstitialId)
                admobAds.loadRewards(this@MainActivity, rewardsId)
            }
        })

        findViewById<Button>(R.id.btnShowBanner).setOnClickListener {
            val bannerView = findViewById<RelativeLayout>(R.id.bannerView)
            admobAds.showBanner(
                this,
                bannerView,
                SizeBanner.SMALL,
                bannerId,
                object : CallbackAds() {
                    override fun onAdFailedToLoad(error: String?) {
                        super.onAdFailedToLoad(error)
                    }
                    override fun onAdLoaded() {
                        super.onAdLoaded()
                    }
                })
        }

        findViewById<Button>(R.id.btnShowInterstitial).setOnClickListener {
            admobAds.showInterstitial(this, interstitialId, object : CallbackAds() {
                override fun onAdFailedToLoad(error: String?) {

                }
            })
        }

        findViewById<Button>(R.id.btnShowRewards).setOnClickListener {
            admobAds.showRewards(this, rewardsId, object : CallbackAds() {
                override fun onAdFailedToLoad(error: String?) {

                }
            }, object : IRewards {
                override fun onUserEarnedReward(rewardsItem: RewardsItem?) {
                }
            })
        }

        findViewById<Button>(R.id.btnSmallNative).setOnClickListener {
            val nativeView = findViewById<RelativeLayout>(R.id.nativeView)
            admobAds.showNativeAds(
                this,
                nativeView,
                SizeNative.SMALL,
                nativeId,
                object : CallbackAds() {
                    override fun onAdFailedToLoad(error: String?) {

                    }
                })
        }

        findViewById<Button>(R.id.btnSmallNativeRectangle).setOnClickListener {
            val nativeView = findViewById<RelativeLayout>(R.id.nativeView)
            admobAds.showNativeAds(
                this,
                nativeView,
                SizeNative.SMALL_RECTANGLE,
                nativeId,
                object : CallbackAds() {
                    override fun onAdFailedToLoad(error: String?) {

                    }
                })
        }

        findViewById<Button>(R.id.btnShowMediumNative).setOnClickListener {
            val nativeView = findViewById<RelativeLayout>(R.id.nativeView)
            admobAds.showNativeAds(
                this,
                nativeView,
                SizeNative.MEDIUM,
                nativeId,
                object : CallbackAds() {
                    override fun onAdFailedToLoad(error: String?) {

                    }
                })
        }

        findViewById<Button>(R.id.btnShowOpenApp).setOnClickListener {
            appOpenAdManager.showAdIfAvailable(this, appOpenId, object : CallbackOpenAd() {
                override fun onAdFailedToLoad(error: String?) {
                    super.onAdFailedToLoad(error)
                }

                override fun onAdLoaded() {
                    super.onAdLoaded()
                }

                override fun onShowAdComplete() {
                    super.onShowAdComplete()
                }
            })
        }

    }


}