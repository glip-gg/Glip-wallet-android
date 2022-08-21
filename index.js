
const deeplinkHost = 'glipwallet://'

checkWalletAction()

async function checkWalletAction() {

    const params = new Proxy(new URLSearchParams(window.location.search), {
        get: (searchParams, prop) => searchParams.get(prop),
      });
    
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
    await window.croakWallet.init({
        'clientId': clientId,
        chain: chain,
        authNetwork: network
      }
    );
    if (await window.croakWallet.isConnected()) {
        let walletId = await window.croakWallet.getWalletID()
        let userInfo = await window.croakWallet.getUserInfo();
        onWalletLogin(walletId, JSON.stringify(userInfo))
    } else {
        window.croakWallet.login(provider, window.location.href)
    }
}

async function walletLogout(provider) {
    console.log('wallet logout', provider)
    setMessage('Logging out..')
    await window.croakWallet.logout(provider)
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
    console.log('showing wallet ui')
    setMessage(`Wallet UI will be shown here when implemented`)
}

function onWalletLogin(walletId, userInfo) {
    let deeplink = deeplinkHost + `walletConnected?walletId=${walletId}&userInfo=${userInfo}`
    console.log('wallet connected: opening deeplink', deeplink)
    window.location = deeplink
}   

function onWalletLogout() {
    let deeplink = deeplinkHost + `loggedOut`
    console.log('wallet logged out: opening deeplink', deeplink)
    window.location = deeplink
} 

function onSignTx(signedTx) {
     // base64 encoded signed tx
     let deeplink = deeplinkHost + `walletTxSigned?signedTx=${signedTx}`
     console.log('wallet signed tx: opening deeplink', deeplink)
     window.location = deeplink
}

function onSignMessage(signedMessage) {
    // base64 encoded signed message
    let deeplink = deeplinkHost + `walletMessageSigned?signedMessage=${signedMessage}`
    console.log('wallet signed message: opening deeplink', deeplink)
    window.location = deeplink
}

function setMessage(message) {
    document.getElementById('message').innerHTML = message
}


