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
implementation 'com.github.glip-gg:Glip-wallet-android:v0.2'

```

### Directly including lib with aar

A prebuilt `aar` is also published in releases. Latest aar can be downloaded from the Release page - https://github.com/glip-gg/Glip-wallet-android/releases/

Using aar in your app - 

Put `wallet-release.aar` in app/libs folder

And add following in app level build.gradle

```
implementation(name: 'wallet-release', ext: 'aar')
```


### SDK Setup

Add redirect scheme meta-data to your application's AndroidManifest.xml
```xml
<meta-data android:name="glip.gg.wallet.redirect.scheme" android:value="enter_your_scheme_here" />
```

Next, add intent filter for a library included activity. Add this in your app's AndroidManifest.xml
```xml
 <activity
            android:launchMode="singleTop"
            android:name="glip.gg.wallet.WalletInteractionActivity"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.VIEW" />
                <category android:name="android.intent.category.DEFAULT" />
                <category android:name="android.intent.category.BROWSABLE" />
                <data android:scheme="enter_your_scheme_here" />
            </intent-filter>
        </activity>
```

Note that to pur same `scheme` value in both meta-data and intent-filter's android:scheme value. This scheme allows your app to handle wallet interactions uniquely if multiple apps have Glip Wallet SDK integrated.

### Initialization
First make sure that you have created a clientId already.

This initialization can be done in your Application class or anywhere else before interacting with any other wallet methods.

```kotlin
GlipWallet.init(appContext, CLIENT_ID, 137)
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

`demo` module in this repo contains the sample usage of Glip wallet sdk.