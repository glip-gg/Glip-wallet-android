package glip.gg.wallet

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Base64
import android.util.Log
import androidx.core.content.edit
import glip.gg.wallet.GlipWallet.decodeBase64
import org.json.JSONObject
import java.lang.Exception
import android.os.Bundle

import android.content.pm.PackageManager

import android.content.pm.ApplicationInfo
import java.lang.IllegalArgumentException


object GlipWallet {

    private const val TAG = "GlipWallet"

    private lateinit var clientId: String
    private lateinit var chain: String
    private lateinit var network: String
    private lateinit var redirectScheme: String

    private lateinit var preferences: SharedPreferences

    private const val PREF_WALLET_CONNECTED = "glip_wallet_connected"
    private const val PREF_USER_INFO = "glip_wallet_user_info"

    private const val BASE_URL = "https://glip.gg/wallet-android/"
    private const val WALLET_HOST_URL = "https://glip.gg/wallet-host/"

    private const val REDIRECT_SCHEME = "redirect_scheme"
    private const val METADATA_KEY_REDIRECT_SCHEME = "glip.gg.wallet.redirect.scheme"

    interface WalletConnectedListener {
        fun onWalletConnected(walletId: String, userInfo: WalletUserInfo?)
        fun onCancelled()
    }

    interface WalletLogoutListener {
        fun onWalletLogout()
    }

    interface WalletSignTransactionListener {
        fun onTransactionSigned(signedTransaction: String)
        fun onCancelled()
    }

    interface WalletSignMessageListener {
        fun onMessageSigned(signedMessage: String)
        fun onCancelled()
    }

    fun init(context: Context, clientId: String, chain: Chain) {
        this.clientId = clientId
        this.chain = chain.name.lowercase()
        this.network = "cyan"
        try {
            val ai: ApplicationInfo = context.packageManager.getApplicationInfo(
                context.packageName,
                PackageManager.GET_META_DATA
            )
            val bundle = ai.metaData
            redirectScheme = bundle.getString(METADATA_KEY_REDIRECT_SCHEME) ?: throw IllegalArgumentException()
        } catch (e: Exception) {
            throw IllegalArgumentException("Redirect scheme not set. Please configure <meta-data android:name=\"${METADATA_KEY_REDIRECT_SCHEME}\" in your manifest")
        }

        preferences = context.getSharedPreferences("glip.gg.wallet", Context.MODE_PRIVATE)
    }

    fun login(context: Context, provider: Provider, listener: WalletConnectedListener) {
        Log.d(TAG, "login requested")
        val url =
            "${BASE_URL}?action=login&chain=$chain&network=$network&clientId=$clientId&provider=${provider.name.lowercase()}"
        launchInteraction(context, url, { data ->
            Log.d(TAG, "login data received: $data")
            if (data.host == "walletConnected") {
                val walletId = data.getQueryParameter("walletId")
                val userInfo = data.getQueryParameter("userInfo")
                Log.d(TAG, "walletId: $walletId")
                Log.d(TAG, "userInfo: $userInfo")
                if (walletId != null && userInfo != null) {
                    preferences.edit {
                        putBoolean(PREF_WALLET_CONNECTED, true)
                        putString(PREF_USER_INFO, userInfo)
                    }
                    listener.onWalletConnected(walletId, serializeUserInfo(userInfo))
                }
            }
        }, {
            listener.onCancelled()
        })
    }

    fun logout(context: Context, provider: Provider, listener: WalletLogoutListener) {
        Log.d(TAG, "logout requested")
        val url =
            "${BASE_URL}?action=logout&provider=${provider.name.lowercase()}"
        launchInteraction(context, url, { data ->
            Log.d(TAG, "logout data received: $data")
            if (data.host == "loggedOut") {
                preferences.edit {
                    putBoolean(PREF_WALLET_CONNECTED, false)
                    putString(PREF_USER_INFO, null)
                }
                listener.onWalletLogout()
            }
        }, {

        })
    }

    fun signMessage(context: Context, message: String, listener: WalletSignMessageListener) {
        Log.d(TAG, "sign message requested")
        val url =
            "${BASE_URL}?action=signMessage&message=${message.encodeBase64()}"
        launchInteraction(context, url, { data ->
            Log.d(TAG, "sign message data received: $data")
            if (data.host == "walletMessageSigned") {
                val signedData = data.getQueryParameter("signedMessage")
                if (signedData != null) {
                    listener.onMessageSigned(signedData.decodeBase64())
                }
            }
        }, {
            listener.onCancelled()
        })
    }

    fun signTransaction(context: Context, txData: String, listener: WalletSignTransactionListener) {
        Log.d(TAG, "sign tx requested")
        val url =
            "${BASE_URL}?action=signTx&txData=${txData.encodeBase64()}"
        launchInteraction(context, url, { data ->
            Log.d(TAG, "sign tx data received: $data")
            if (data.host == "walletTxSigned") {
                val signedData = data.getQueryParameter("signedTx")
                if (signedData != null) {
                    listener.onTransactionSigned(signedData.decodeBase64())
                }
            }
        }, {
            listener.onCancelled()
        })
    }

    fun showWallet(context: Context) {
        Log.d(TAG, "show wallet requested")
        launchInteraction(context, WALLET_HOST_URL, {}, {})
    }

    fun isConnected() = preferences.getBoolean(PREF_WALLET_CONNECTED, false)
    fun getUserInfo() = serializeUserInfo(preferences.getString(PREF_USER_INFO, null))

    private fun launchInteraction(context: Context, url: String, callback: ((data: Uri) -> Unit), cancelCallback: () -> Unit) {
        var uri = Uri.parse(url)
        if (uri.getQueryParameter(REDIRECT_SCHEME).isNullOrEmpty()) {
            uri = uri.buildUpon().appendQueryParameter(REDIRECT_SCHEME, redirectScheme).build()
        }
        WalletInteractionActivity.launch(
            context,
            uri.toString(),
            object : WalletInteractionActivity.WalletActionCallback {
                override fun onWalletActionComplete(data: Uri) {
                    callback(data)
                }

                override fun onWalletActionCancelled() {
                    cancelCallback()
                }
            })
    }

    private fun String.decodeBase64(): String {
        return Base64.decode(this, Base64.DEFAULT).toString(charset("UTF-8"))
    }

    private fun String.encodeBase64(): String {
        return Base64.encodeToString(this.toByteArray(charset("UTF-8")), Base64.DEFAULT)
    }

    private fun serializeUserInfo(userInfo: String?): WalletUserInfo? {
        if (userInfo == null) return null
        try {
            val jsonUserInfo = JSONObject(userInfo)
            return WalletUserInfo(
                jsonUserInfo.optString("email"),
                jsonUserInfo.optString("name"),
                jsonUserInfo.optString("profileImage"),
                jsonUserInfo.optString("publicAddress")
            )
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun internalLogout(context: Context) {
        context.getSharedPreferences("glip.gg.wallet", Context.MODE_PRIVATE).edit {
            putBoolean(PREF_WALLET_CONNECTED, false)
            putString(PREF_WALLET_CONNECTED, null)
        }
    }

}