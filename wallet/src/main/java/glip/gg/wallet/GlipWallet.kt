package glip.gg.wallet

import android.content.Context
import android.net.Uri
import android.util.Log
import java.util.*

object GlipWallet {

    private const val TAG = "GlipWallet"

    private lateinit var clientId: String
    private lateinit var chain: String
    private lateinit var network: String

    private const val BASE_URL = "https://glip-gg.github.io/Glip-wallet-android/"

    interface WalletConnectedListener {
        fun onWalletConnected(walletId: String, userInfo: String)
    }

    interface WalletLogoutListener {
        fun onWalletLogout()
    }

    fun init(clientId: String, chain: Chain, network: Network) {
        this.clientId = clientId
        this.chain = chain.name.lowercase(Locale.getDefault())
        this.network = network.name.lowercase(Locale.getDefault())
    }

    fun login(context: Context, provider: Provider, listener: WalletConnectedListener) {
        Log.d(TAG, "login requested")
        val url =
            "${BASE_URL}?action=login&chain=$chain&network=$network&clientId=$clientId&provider=${provider.name.lowercase()}"
        launchInteraction(context, url) { data ->
            Log.d(TAG, "login data received: $data")
            if (data.host == "walletConnected") {
                val walletId = data.getQueryParameter("walletId")
                val userInfo = data.getQueryParameter("userInfo")
                Log.d(TAG, "walletId: $walletId")
                Log.d(TAG, "userInfo: $userInfo")
                if (walletId != null && userInfo != null) {
                    listener.onWalletConnected(walletId, userInfo)
                }
            }
        }
    }

    fun logout(context: Context, provider: Provider, listener: WalletLogoutListener) {
        Log.d(TAG, "logout requested")
        val url =
            "${BASE_URL}?action=logout?provider=${provider.name.lowercase()}"
        launchInteraction(context, url) { data ->
            Log.d(TAG, "logout data received: $data")
            if (data.host == "loggedOut") {
                listener.onWalletLogout()
            }
        }
    }

    private fun launchInteraction(context: Context, url: String, callback: ((data: Uri) -> Unit)) {
        WalletInteractionActivity.launch(
            context,
            url,
            object : WalletInteractionActivity.WalletActionCallback {
                override fun onWalletActionComplete(data: Uri) {
                    callback(data)
                }
            })
    }

}