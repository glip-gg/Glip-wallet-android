package glip.gg.wallet

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent

class WalletInteractionActivity : AppCompatActivity() {

    interface WalletActionCallback {
        fun onWalletActionComplete(data: Uri)
        fun onWalletActionCancelled()
    }

    companion object {
        private const val TAG = "GlipWallet"

        private var actionCallback: WalletActionCallback? = null

        fun launch(context: Context, url: String, actionCallback: WalletActionCallback) {
            this.actionCallback = actionCallback
            context.startActivity(
                Intent(
                    context,
                    WalletInteractionActivity::class.java
                ).apply {
                    data = Uri.parse(url)
                }
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleIntentData(intent.data)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleIntentData(intent?.data)
    }

    private fun handleIntentData(uri: Uri?) {
        if (uri == null) return
        Log.d(TAG, uri.toString())

        val host = uri.host
        val scheme = uri.scheme

        Log.d(TAG, "Host: $host")

        if (scheme == GlipWallet.getRedirectScheme(this)) {
            if (host == "loggedOut") {
               GlipWallet.internalLogout(this)
            }
            actionCallback?.onWalletActionComplete(uri)
            return
        } else {
            launchBrowser(this, uri)
            return
        }
    }

    private fun launchBrowser(context: Context, url: Uri) {
        val builder: CustomTabsIntent.Builder = CustomTabsIntent.Builder()

        val colorInt: Int = Color.parseColor("#131422")
        val defaultColors: CustomTabColorSchemeParams = CustomTabColorSchemeParams.Builder()
            .setToolbarColor(colorInt)
            .build()

        builder.setUrlBarHidingEnabled(true)
        builder.setShowTitle(true)
        builder.setDefaultColorSchemeParams(defaultColors)

        val customTabsIntent: CustomTabsIntent = builder.build()
        customTabsIntent.intent.data = url
        (context as AppCompatActivity).startActivityForResult(customTabsIntent.intent, 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == Activity.RESULT_CANCELED) {
            Log.d(TAG, "canceled, finishing interaction")
            actionCallback?.onWalletActionCancelled()
            finish()
        }
    }
}