package glip.gg.wallet

import android.content.Context
import android.net.Uri
import android.util.Base64
import android.util.Log

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

    interface WalletSignTransactionListener {
        fun onTransactionSigned(signedTransaction: String)
    }

    interface WalletSignMessageListener {
        fun onMessageSigned(signedMessage: String)
    }

    fun init(clientId: String, chain: Chain, network: Network) {
        this.clientId = clientId
        this.chain = chain.name.lowercase()
        this.network = network.name.lowercase()
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
            "${BASE_URL}?action=logout&provider=${provider.name.lowercase()}"
        launchInteraction(context, url) { data ->
            Log.d(TAG, "logout data received: $data")
            if (data.host == "loggedOut") {
                listener.onWalletLogout()
            }
        }
    }

    fun signMessage(context: Context, message: String, listener: WalletSignMessageListener) {
        Log.d(TAG, "sign message requested")
        val url =
            "${BASE_URL}?action=signMessage&message=${message.encodeBase64()}"
        launchInteraction(context, url) { data ->
            Log.d(TAG, "sign message data received: $data")
            if (data.host == "walletMessageSigned") {
                val signedData = data.getQueryParameter("signedMessage")
                if (signedData != null) {
                    listener.onMessageSigned(signedData.decodeBase64())
                }
            }
        }
    }

    fun signTransaction(context: Context, txData: String, listener: WalletSignTransactionListener) {
        Log.d(TAG, "sign tx requested")
        val url =
            "${BASE_URL}?action=signTx&message=${txData.encodeBase64()}"
        launchInteraction(context, url) { data ->
            Log.d(TAG, "sign tx data received: $data")
            if (data.host == "walletTxSigned") {
                val signedData = data.getQueryParameter("signedTx")
                if (signedData != null) {
                    listener.onTransactionSigned(signedData.decodeBase64())
                }
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

    private fun String.decodeBase64(): String {
        return Base64.decode(this, Base64.DEFAULT).toString(charset("UTF-8"))
    }

    private fun String.encodeBase64(): String {
        return Base64.encodeToString(this.toByteArray(charset("UTF-8")), Base64.DEFAULT)
    }

}