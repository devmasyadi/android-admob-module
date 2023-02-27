@file:Suppress("DEPRECATION")

package com.adsmanager.admobmodule

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.adsmanager.admob.AdmobAds
import com.adsmanager.admob.AdmobOpenAd
import com.adsmanager.core.iadsmanager.IInitialize

class MyApplication : Application(), Application.ActivityLifecycleCallbacks, LifecycleObserver {


    private lateinit var appOpenAdManager: AdmobOpenAd

    override fun onCreate() {
        super.onCreate()
        registerActivityLifecycleCallbacks(this)

        AdmobAds().initialize(this, null, object : IInitialize {
            override fun onInitializationComplete() {

            }
        })

        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        appOpenAdManager = AdmobOpenAd()
    }

    @Suppress("DEPRECATION")
    /** LifecycleObserver method that shows the app open ad when the app moves to foreground. */
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onMoveToForeground() {
        // Show the ad (if available) when the app moves to foreground.
        appOpenAdManager.currentActivity?.let {
            appOpenAdManager.showAdIfAvailable(
                it,
                ConfigAds.adUnitOpenId
            )
        }
    }

    /** ActivityLifecycleCallback methods. */
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}

    override fun onActivityStarted(activity: Activity) {
        Log.e("HALLO", "onActivityStarted")
        // An ad activity is started when an ad is showing, which could be AdActivity class from Google
        // SDK or another activity class implemented by a third party mediation partner. Updating the
        // currentActivity only when an ad is not showing will ensure it is not an ad activity, but the
        // one that shows the ad.
        if (!appOpenAdManager.isShowingAd) {
            appOpenAdManager.currentActivity = activity
        }
    }

    override fun onActivityResumed(activity: Activity) {}

    override fun onActivityPaused(activity: Activity) {}

    override fun onActivityStopped(activity: Activity) {}

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

    override fun onActivityDestroyed(activity: Activity) {}
}