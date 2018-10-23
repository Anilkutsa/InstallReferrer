package com.sheldononpot.mydemoapp

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity;
import android.view.Menu
import android.view.MenuItem
import com.mixpanel.android.mpmetrics.MixpanelAPI

import kotlinx.android.synthetic.main.activity_splash.*
import kotlinx.android.synthetic.main.content_splash.*
import org.json.JSONObject
import android.content.Intent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.support.v4.content.LocalBroadcastManager

class SplashActivity : AppCompatActivity() {

    companion object {
        public val TAG = "DemoApp"

        public val KEY_UTM_SOURCE = "utm_source"
        public val KEY_UTM_MEDIUM = "utm_medium"
        public val KEY_UTM_TERM = "utm_term"
        public val KEY_UTM_CONTENT = "utm_content"
        public val KEY_UTM_CAMPAIGN = "utm_campaign"

        public val INSTALL_EVENT = "install_data";
        public val STRING_EVENT = "string_data";

        public val STRING_EXTRA = "string_extra";
    }

    val projectToken = "4e2d0a8450634c1dd19620890cbc3594"
    var mixpanel: MixpanelAPI? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        setSupportActionBar(toolbar)

        mixpanel = MixpanelAPI.getInstance(this, projectToken)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

        val timer = object : Thread() {
            override fun run() {
                try {
                    Thread.sleep(3000)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                } finally {
                    val intent = Intent(applicationContext, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }
        timer.start()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(applicationContext).registerReceiver(receiver, IntentFilter(INSTALL_EVENT))
        LocalBroadcastManager.getInstance(applicationContext).registerReceiver(receiver, IntentFilter(STRING_EVENT))
    }

    override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(applicationContext).unregisterReceiver(receiver)
    }

    fun logTextEvent(event: String) {
        var text = textView.text.toString()
        textView.setText(text + " \n\n" + event)
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == INSTALL_EVENT) {

                val utmSource = intent.getStringExtra(KEY_UTM_SOURCE)
                val utmMedium = intent.getStringExtra(KEY_UTM_MEDIUM)
                val utmTerm = intent.getStringExtra(KEY_UTM_TERM)
                val utmContent = intent.getStringExtra(KEY_UTM_CONTENT)
                val utmCampaign = intent.getStringExtra(KEY_UTM_CAMPAIGN)

                val allParams = ("utmSource: " + utmSource + ",utmMedium: " + utmMedium
                        + " ,utmTerm" + utmTerm + " ,utmContent: " + utmContent + " ,utmCampaign: " + utmCampaign)

                val props = JSONObject()
                if (utmSource != null) props.put(KEY_UTM_SOURCE, utmSource)
                if (utmMedium != null) props.put(KEY_UTM_MEDIUM, utmMedium)
                if (utmTerm != null) props.put(KEY_UTM_TERM, utmTerm)
                if (utmContent != null) props.put(KEY_UTM_CONTENT, utmContent)
                if (utmCampaign != null) props.put(KEY_UTM_CAMPAIGN, utmCampaign)

                logTextEvent("AllParams from SplashActivity " + allParams)

                mixpanel?.track("Install_Tracking", props);
            } else if (intent.action == STRING_EVENT) {
                logTextEvent(intent.getStringExtra(STRING_EXTRA))
            }
        }
    }
}
