package glip.gg.wallet

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent

class WalletInteractionActivity : AppCompatActivity() {

    interface WalletActionCallback {
        fun onWalletActionComplete(data: Uri)
    }

    companion object {
        private var actionCallback: WalletActionCallback? = null

        private const val URL = "url"

        private  val WALLET_ACTION_CALLBACKS = arrayOf(
            "walletConnected", "walletTxSigned", "walletMessageSigned"
        )

        private val WALLET_ACTIONS = arrayOf(
            "login", "signTx", "signMessage"
        )

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
        val host = uri.host
        if (WALLET_ACTION_CALLBACKS.contains(host)) {
            actionCallback?.onWalletActionComplete(uri)
            return
        }
        if (WALLET_ACTIONS.contains(host)) {
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

        builder.setDefaultColorSchemeParams(defaultColors)

        val customTabsIntent: CustomTabsIntent = builder.build()
        customTabsIntent.launchUrl(context, url)
    }
}