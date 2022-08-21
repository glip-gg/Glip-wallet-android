
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
        case 'signTx':
            break;
        case 'signMessage':
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

function walletSignTx() {

}

function walletSignMessage() {

}


function onWalletLogin(walletId, userInfo) {
    let deeplink = deeplinkHost + `walletConnected?walletId=${walletId}&userInfo=${userInfo}`
    console.log('wallet connected: opening deeplink', deeplink)
    window.location = deeplink

}   

function onSignTx() {

}

function onSignMessage() {

}

function setMessage(message) {
    document.getElementById('message').innerHTML = message
}


