# Glip Wallet Android SDK

Android SDK and demo for using Glip Wallet in Android project

## Install

### Using Jitpack

Add Jitpack to your project level `build.gradle` file

```
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```

Add wallet sdk dependency in your app level `build.gradle` file
```
implementation 'com.github.glip-gg:Glip-wallet-android:v0.1'

```

### Directly including lib with aar

A prebuilt `aar` is also published in releases. Latest aar can be downloaded from the Release page - https://github.com/glip-gg/Glip-wallet-android/releases/

Using aar in your app - 

Put `wallet-release.aar` in app/libs folder

And add following in app level build.gradle

```
implementation(name: 'wallet-release', ext: 'aar')
```

## Usage

First make sure that you have created a clientId already.

### Initialization

This initialization can be done in your Application class or anywhere else before interacting with any other wallet methods.

```kotlin
GlipWallet.init(appContext, CLIENT_ID, Chain.POLYGON, Network.TESTNET)
```

### Login and connnect wallet

```kotlin
GlipWallet.login(activityContext, Provider.GOOGLE, object : GlipWallet.WalletConnectedListener {
                override fun onWalletConnected(walletId: String, userInfo: WalletUserInfo?) {
                    Log.d(TAG, "Wallet connected: wallet_id:  $walletId\nUser info: ${userInfo?.toString()}")
                }
            })
```

### Show wallet

```kotlin
GlipWallet.showWallet(activityContext)
```

### Signing transaction

Construct the transaction request payload and call `signTransaction` method. This will sign the transaction request with user's private key. Transaction can then be sent to the chain using your own provider.

```kotlin
GlipWallet.signTransaction(activityContext, txToSign, object : GlipWallet.WalletSignTransactionListener {
                override fun onTransactionSigned(signedTransaction: String) {
                    Log.d(TAG, "Signed transaction\n${signedTransaction}")

                }
            })
```


### Signing message

```kotlin
val messageToSign = "This is a message from Glip wallet android demo. Please sign this message"
GlipWallet.signMessage(activityContext, messageToSign, object : GlipWallet.WalletSignMessageListener {
                override fun onMessageSigned(signedMessage: String) {
                    Log.d(TAG, "Signed message\n${signedMessage}")
                }
            })
```


### Logout

```kotlin
GlipWallet.logout(activityContext, Provider.GOOGLE, object : GlipWallet.WalletLogoutListener {
                override fun onWalletLogout() {
                    Log.d(TAG, "Logout success")
                }
            })
```

### isConnected()
```kotlin
GlipWallet.isConnected()
```

### getUserInfo()

getUserInfo() returns user's email, name, profile image url and wallet's public address

```kotlin
GlipWallet.getUserInfo()
```

## Wallet sdk usage demo app

`demo` module in this repo contains the sample usage of Glip wallet sdk. Demo can also be downloaded directly from the releases section. https://github.com/glip-gg/Glip-wallet-android/releases/download/v0.1/demo-release.apk