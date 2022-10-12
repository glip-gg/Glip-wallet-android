
window.walletSignTx = async function walletSignTx(txData, clientId, chainId) {
    setMessage(`Signing transaction`);
    await initialiseWallet(clientId, chainId)
    let signer = await window.glipWalletSDK?.getSigner();
    let signedTx = await signer?.signTransaction(
        JSON.parse(txData), '', true);
    onWalletActionResult('signTx', signedTx)
}


window.walletSignMessage = async function walletSignMessage(message, clientId, chainId) {
    console.log('signing message', message)
    setMessage(`Signing message...`)
    await initialiseWallet(clientId, chainId)
    let signer = await window.glipWalletSDK?.getSigner();
    let signedMessage = await signer?.signMessage(message, true);
    onWalletActionResult('signMessage', signedMessage)
}


window.walletSendTx = async function walletSendTx(txData, clientId, chainId) {
    setMessage(`Sending transaction`);
    await initialiseWallet(clientId, chainId)
    let signer = await window.glipWalletSDK?.getSigner();
    let signedTx = await signer?.sendTransaction(JSON.parse(txData));
    onWalletActionResult('sendTx', signedTx)
}

async function initialiseWallet(clientId, chainId) {
    await window.glipWalletSDK.init({
        'clientIdentifier': clientId,
        chainId: chainId,
        authNetwork: 'cyan'
    }
    );
}

function onWalletActionResult(action, result) {
    window.WalletActionInterface.onWalletActionCallback(action, result);
}

function setMessage(message) {
    document.getElementById('message').innerHTML = message
}




async function checkWalletAction() {
    const params = new Proxy(new URLSearchParams(window.location.search), {
        get: (searchParams, prop) => searchParams.get(prop),
      });

    let redirectScheme = params.redirect_scheme
    let actionType = params.actionType;
    
    console.log('wallet action', actionType)

    switch (actionType) {
        case 'signedMessage':
            //walletLogin()
            break;
        case 'signedTransaction':
            //walletSignTx()
            break;
        default:
            window.walletSignMessage('gg');
            break;

    }
}

checkWalletAction()


