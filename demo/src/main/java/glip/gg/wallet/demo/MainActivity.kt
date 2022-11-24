package glip.gg.wallet.demo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import glip.gg.wallet.*
import glip.gg.wallet.demo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    companion object {
        private const val CLIENT_ID = "63020e1ef81e3742a278846a"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        GlipWallet.init(this, CLIENT_ID, Chain.POLYGON)

        binding.btnConnectWallet.setOnClickListener {
            GlipWallet.login(this, Provider.GOOGLE, object : GlipWallet.WalletConnectedListener {
                override fun onWalletConnected(walletId: String, userInfo: WalletUserInfo?) {
                    binding.tvStatus.text = "Wallet connected: wallet_id:  $walletId\nUser info: ${userInfo?.toString()}"
                }

                override fun onCancelled() {

                }
            })
        }

        binding.btnLogout.setOnClickListener {
            GlipWallet.logout(this, Provider.GOOGLE, object : GlipWallet.WalletLogoutListener {
                override fun onWalletLogout() {
                    binding.tvStatus.text = "Logout success"
                }
            })
        }

        binding.btnSendTx.setOnClickListener {
            val txToSign = "{'from': '0x0', 'data': '0x0'}"
            GlipWallet.sendTransaction(this, txToSign, object : GlipWallet.WalletSignTransactionListener {
                override fun onTransactionSigned(signedTransaction: String) {
                    binding.tvStatus.text = "sent transaction\n${signedTransaction}"
                }

                override fun onCancelled() {

                }
            })
        }

        binding.btnSignTx.setOnClickListener {
            val txToSign = "{'from': '0x0', 'data': '0x0'}"
            GlipWallet.signTransaction(this, txToSign, object : GlipWallet.WalletSignTransactionListener {
                override fun onTransactionSigned(signedTransaction: String) {
                    binding.tvStatus.text = "Signed transaction\n${signedTransaction}"
                }

                override fun onCancelled() {

                }
            })
        }

        binding.btnSignMessage.setOnClickListener {
            val txToSign = "This is a message from Glip wallet android demo. Please sign this message"
            GlipWallet.signMessage(this, txToSign, object : GlipWallet.WalletSignMessageListener {
                override fun onMessageSigned(signedMessage: String) {
                    binding.tvStatus.text = "Signed message\n${signedMessage}"
                }

                override fun onCancelled() {

                }
            })
        }

        binding.btnSignPersonalMessage.setOnClickListener {
            val txToSign = "This is a message from Glip wallet android demo. Please sign this message"
            GlipWallet.signMessage(this, txToSign, object : GlipWallet.WalletSignMessageListener {
                override fun onMessageSigned(signedMessage: String) {
                    binding.tvStatus.text = "Signed personal message\n${signedMessage}"
                }

                override fun onCancelled() {

                }
            })
        }

        binding.btnShowWallet.setOnClickListener {
            GlipWallet.showWallet(this)
        }

    }
}