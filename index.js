
var redirectScheme = 'glipwallet://'

checkWalletAction()

async function checkWalletAction() {

    const params = new Proxy(new URLSearchParams(window.location.search), {
        get: (searchParams, prop) => searchParams.get(prop),
      });

    redirectScheme = params.redirect_scheme
    let action = params.action;
    
    console.log('wallet action', action)

    switch (action) {
        case 'login':
            walletLogin(params.clientId, params.chain, params.network, params.provider)
            break;
        case 'logout':
            walletLogout(params.provider)
            break;
        case 'signTx':
            // decode base64
            walletSignTx(atob(params.txData))
            break;
        case 'signMessage':
            // decode base64
            walletSignMessage(atob(params.message))
            break;
        case 'showWallet': 
            showWalletUI();
            break;
    }
}

async function walletLogin(clientId, chain, network, provider) {
    console.log('wallet login', chain, network)
    setMessage('Logging in..')
    await window.glipWalletSDK.init({
        'clientIdentifier': clientId,
        chain: chain,
        authNetwork: network
      }
    );
    if (await window.glipWalletSDK.isConnected()) {
        let walletId = await window.glipWalletSDK.getWalletID()
        let userInfo = await window.glipWalletSDK.getUserInfo();
        onWalletLogin(walletId, JSON.stringify(userInfo))
    } else {
        window.glipWalletSDK.login(provider, window.location.href)
    }
}

async function walletLogout(provider) {
    console.log('wallet logout', provider)
    setMessage('Logging out..')
    await window.glipWalletSDK.logout(provider)
    onWalletLogout()
}

function walletSignTx(txData) {
    console.log('signing tx', txData)
    setMessage(`Signing transaction\n\n${txData}`)
    setTimeout(() => {
        onSignTx(btoa('Placeholder, signed transaction data will be here when implemented'))
    }, 2000)
}

function walletSignMessage(message) {
    console.log('signing message', message)
    setMessage(`Signing message\n\n${message}`)
    setTimeout(() => {
        onSignMessage(btoa('Placeholder, signed message will be here when implemented'))
    }, 2000)
}

function showWalletUI() {
    window.location.replace('https://glip.gg/wallet-host')
}

function onWalletLogin(walletId, userInfo) {
    let deeplink = redirectScheme + `walletConnected?walletId=${walletId}&userInfo=${userInfo}`
    console.log('wallet connected: opening deeplink', deeplink)
    window.location = deeplink
}   

function onWalletLogout() {
    let deeplink = redirectScheme + `loggedOut`
    console.log('wallet logged out: opening deeplink', deeplink)
    window.location = deeplink
} 

function onSignTx(signedTx) {
     // base64 encoded signed tx
     let deeplink = redirectScheme + `walletTxSigned?signedTx=${signedTx}`
     console.log('wallet signed tx: opening deeplink', deeplink)
     window.location = deeplink
}

function onSignMessage(signedMessage) {
    // base64 encoded signed message
    let deeplink = redirectScheme + `walletMessageSigned?signedMessage=${signedMessage}`
    console.log('wallet signed message: opening deeplink', deeplink)
    window.location = deeplink
}

function setMessage(message) {
    document.getElementById('message').innerHTML = message
}


