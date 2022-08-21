package glip.gg.wallet

import android.content.Context
import android.net.Uri
import android.util.Log

object GlipWallet {

    private const val TAG = "GlipWallet"

    private lateinit var clientId: String
    private lateinit var chain: Chain
    private lateinit var network: Network

    private const val BASE_URL = "https://glip-gg.github.io/glip-wallet-android"

    interface WalletConnectedListener {
        fun onWalletConnected(walletId: String, userInfo: String)
    }

    fun init(clientId: String, chain: Chain, network: Network) {
        this.clientId = clientId
        this.chain = chain
        this.network = network
    }

    fun login(context: Context, provider: Provider, listener: WalletConnectedListener) {
        Log.d(TAG, "login requested")
        val url =
            "${BASE_URL}?action=login&chain=$chain&network=$network&clientId=$clientId&provider=$provider"
        launchInteraction(context, url) { data ->
            Log.d(TAG, "login data received: $data")
            if (data.host == "login") {
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

    private fun launchInteraction(context: Context, url: String, callback: ((data: Uri) -> Unit)) {
        WalletInteractionActivity.launch(context, url, object : WalletInteractionActivity.WalletActionCallback {
            override fun onWalletActionComplete(data: Uri) {
                callback(data)
            }
        })
    }

}