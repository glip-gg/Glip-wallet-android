
const REDIRECT_SCHEME_KEY = 'wallet_android_redirect_scheme'

var redirectScheme = localStorage.getItem(REDIRECT_SCHEME_KEY)

window.walletSignTx = async function walletSignTx(txData, clientId, chainId) {
    let txToSign = getDecodedMessage(txData)
    setMessage(`Signing transaction`);
    await initialiseWallet(clientId, chainId)
    let signer = await window.glipWalletSDK?.getSigner();
    let signedTx = await signer?.signTransaction(
        JSON.parse(txToSign), '', true);
    onWalletActionResult(true, 'signTx', signedTx)
}

window.walletSignMessage = async function walletSignMessage(message, clientId, chainId) {
    let messageToSign = getDecodedMessage(message)
    console.log('signing message', messageToSign)
    setMessage(`Signing message...`)
    await initialiseWallet(clientId, chainId)
    let signer = await window.glipWalletSDK?.getSigner();
    let signedMessage = await signer?.signMessage(messageToSign, true);
    onWalletActionResult(true, 'signMessage', signedMessage)
}

window.walletSignPersonalMessage = async function walletSignPersonalMessage(message, clientId, chainId) {
    let messageToSign = getDecodedMessage(message)
    console.log('signing message', messageToSign)
    setMessage(`Signing message...`)
    await initialiseWallet(clientId, chainId)
    let signer = await window.glipWalletSDK?.getSigner();
    let signedMessage = await signer?.signPersonalMessage(messageToSign, true);
    onWalletActionResult(true, 'signPersonalMessage', signedMessage)
}

window.walletSendTx = async function walletSendTx(txData, clientId, chainId) {
    let txToSend = getDecodedMessage(txData)
    setMessage(`Sending transaction`);
    await initialiseWallet(clientId, chainId)
    let signer = await window.glipWalletSDK?.getSigner();
    let signedTx = await signer?.sendTransaction(JSON.parse(txToSend));
    onWalletActionResult(true, 'sendTx', signedTx)
}

async function initialiseWallet(clientId, chainId) {
    await window.glipWalletSDK.init({
        'clientIdentifier': clientId,
        chainId: chainId,
        authNetwork: 'cyan'
    }
    );
}

async function walletLogin(clientId, chain, network, provider) {
    console.log('wallet login', chain, network)
    setMessage('Logging in..')
    await initialiseWallet(clientId, chain)
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

function showWalletUI() {
    window.location.replace('https://glip.gg/wallet-host')
}

function onWalletActionResult(success, action, result) {
    if (window.WalletActionInterface) {
        window.WalletActionInterface.onWalletActionCallback(success, action, result);
    }
    if (redirectScheme && redirectScheme.length > 0) {
        let deeplink = redirectScheme + `${action}?data=${result}`
        console.log('wallet: opening deeplink', deeplink)
        window.location = deeplink  
    }
}

function setMessage(message) {
    document.getElementById('message').innerHTML = message
}

async function checkWalletAction() {
    const params = new Proxy(new URLSearchParams(window.location.search), {
        get: (searchParams, prop) => searchParams.get(prop),
      });

    // wallet action redirects
    let actionType = params.actionType;
    console.log('wallet redirect action', actionType)
    switch (actionType) {
        case 'signedMessage':
            onWalletActionResult(
                params.signedMessage && params.signedMessage.length > 0, 'signMessage', params.signedMessage)
            
            break;
        case 'signedTransaction':
            onWalletActionResult(
                params.signedTransaction && params.signedTransaction.length > 0, 'signTx', params.signedTransaction)
            break;
        case 'signedPersonalMessage':
            onWalletActionResult(
                params.signedPersonalMessage && params.signedPersonalMessage.length > 0,
                'signPersonalMessage',
                params.signedPersonalMessage)
            break;
        default:
            break;

    }

    // wallet action triggers
    if (params.redirect_scheme) {
        redirectScheme = params.redirect_scheme + "://"
        console.log('redirect scheme', redirectScheme)
        localStorage.setItem(REDIRECT_SCHEME_KEY, redirectScheme)
    }
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
            walletSignTx(params.txData, params.clientId, params.chain)
            break;
        case 'signMessage':
            walletSignMessage(params.message, params.clientId, params.chain)
            break;
        case 'signPersonalMessage':
            walletSignPersonalMessage(params.message, params.clientId, params.chain)
            break;
        case 'sendTx':
            walletSendTx(params.txData, params.clientId, params.chain);
            break;    
        case 'showWallet': 
            showWalletUI();
            break;
    }
}

checkWalletAction()

function getDecodedMessage(message) {
    try {
        return atob(message)
    } catch {
        return message
    }
}


