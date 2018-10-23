package com.sheldononpot.mydemoapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import com.sheldononpot.mydemoapp.SplashActivity.Companion.INSTALL_EVENT
import com.sheldononpot.mydemoapp.SplashActivity.Companion.KEY_UTM_CAMPAIGN
import com.sheldononpot.mydemoapp.SplashActivity.Companion.KEY_UTM_CONTENT
import com.sheldononpot.mydemoapp.SplashActivity.Companion.KEY_UTM_MEDIUM
import com.sheldononpot.mydemoapp.SplashActivity.Companion.KEY_UTM_SOURCE
import com.sheldononpot.mydemoapp.SplashActivity.Companion.KEY_UTM_TERM
import com.sheldononpot.mydemoapp.SplashActivity.Companion.STRING_EVENT
import com.sheldononpot.mydemoapp.SplashActivity.Companion.STRING_EXTRA

class InstallReferrerReceiver() : BroadcastReceiver() {

    val ACTION_INSTALL_REFERRER = "com.android.vending.INSTALL_REFERRER"
    val KEY_REFERRER = "referrer"

    override fun onReceive(context: Context, intent: Intent?) {
        Log.i(SplashActivity.TAG, "InstallReferrerReceiver onReceive called")

        if (intent == null) {
            return
        }

        if (ACTION_INSTALL_REFERRER != intent.action) {
            return
        }

        val extras = intent.extras
        if (intent.extras == null) {
            return
        }

        Log.i(SplashActivity.TAG, "onReceive: KEY_REFERRER " + extras!!.get(KEY_REFERRER)!!)

        if (extras != null && extras.get(KEY_REFERRER) != null) {
            val referrer = intent.getStringExtra("referrer")
            if (referrer != null && referrer != "") {

                Log.i(SplashActivity.TAG, "Referral Received - $referrer")

                val referrerParts = referrer.split("&".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val utmSource = getData(KEY_UTM_SOURCE, referrerParts)
                val utmMedium = getData(KEY_UTM_MEDIUM, referrerParts)
                val utmTerm = getData(KEY_UTM_TERM, referrerParts)
                val utmContent = getData(KEY_UTM_CONTENT, referrerParts)
                val utmCampaign = getData(KEY_UTM_CAMPAIGN, referrerParts)

                val allParams = ("utmSource: " + utmSource + " ,utmMedium: " + utmMedium
                        + " ,utmTerm" + utmTerm + ",utmContent: " + utmContent + " ,utmCampaign: " + utmCampaign)
                Log.i(SplashActivity.TAG, "AllParams from InstallReferrer " + allParams)

                val stringIntent = Intent(STRING_EVENT)
                stringIntent.putExtra(STRING_EXTRA, "AllParams from InstallReferrer " + allParams)
                LocalBroadcastManager.getInstance(context).sendBroadcast(stringIntent)

                val ie = InstallEvent(true)
                if (utmSource != null && utmSource !== "") ie.utmSource = utmSource else null
                if (utmMedium != null && utmMedium !== "") ie.utmMedium = utmMedium else null
                if (utmTerm != null && utmTerm !== "") ie.utmTerm = utmTerm else null
                if (utmContent != null && utmContent !== "") ie.utmContent = utmContent else null
                if (utmCampaign != null && utmCampaign !== "") ie.utmCampaign = utmCampaign else null

                val installDataIntent = Intent(INSTALL_EVENT)
                installDataIntent.putExtra(KEY_UTM_SOURCE, utmSource)
                installDataIntent.putExtra(KEY_UTM_MEDIUM, utmMedium)
                installDataIntent.putExtra(KEY_UTM_TERM, utmTerm)
                installDataIntent.putExtra(KEY_UTM_CONTENT, utmContent)
                installDataIntent.putExtra(KEY_UTM_CAMPAIGN, utmCampaign)
                LocalBroadcastManager.getInstance(context).sendBroadcast(installDataIntent)
            }
        }
    }

    private fun getData(key: String, allData: Array<String>): String {
        for (selected in allData)
            if (selected.contains(key)) {
                return selected.split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
            }
        return ""
    }
}