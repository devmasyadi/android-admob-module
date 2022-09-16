package com.adsmanager.admob

import android.app.Activity
import com.google.android.ump.ConsentForm
import com.google.android.ump.ConsentInformation
import com.google.android.ump.UserMessagingPlatform
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

object Utils {
    fun md5(s: String): String {
        try {
            // Create MD5 Hash
            val digest = MessageDigest
                .getInstance("MD5")
            digest.update(s.toByteArray())
            val messageDigest = digest.digest()

            // Create Hex String
            val hexString = StringBuffer()
            for (i in messageDigest.indices) {
                var h = Integer.toHexString(0xFF and messageDigest[i].toInt())
                while (h.length < 2) h = "0$h"
                hexString.append(h)
            }
            return hexString.toString()
        } catch (e: NoSuchAlgorithmException) {
            //Logger.logStackTrace(TAG,e);
        }
        return ""
    }

    fun loadForm(activity: Activity, consentInformation: ConsentInformation) {
        UserMessagingPlatform.loadConsentForm(
            activity,
            { consentForm: ConsentForm ->
                if (consentInformation.consentStatus == ConsentInformation.ConsentStatus.REQUIRED) {
                    consentForm.show(
                        activity
                    ) { // Handle dismissal by reloading form.
                        loadForm(activity, consentInformation)
                    }
                }
            }
        ) { }
    }
}