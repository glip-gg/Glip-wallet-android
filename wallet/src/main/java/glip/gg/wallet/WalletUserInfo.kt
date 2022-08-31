package glip.gg.wallet

import androidx.annotation.Keep

@Keep
data class WalletUserInfo(
    val email: String? = null,
    val name: String? = null,
    val profileImage: String? = null,
    val publicAddress: String? = null
) {
    override fun toString(): String {
        return "email: ${email.orEmpty()}, name: ${name.orEmpty()}, image: ${profileImage.orEmpty()}, address: ${publicAddress.orEmpty()}"
    }
}