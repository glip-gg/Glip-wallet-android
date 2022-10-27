
window.walletSignTx = async function walletSignTx(txData, clientId, chainId) {
    setMessage(`Signing transaction`);
    await initialiseWallet(clientId, chainId)
    let signer = await window.glipWalletSDK?.getSigner();
    let signedTx = await signer?.signTransaction(
        JSON.parse(txData), '', true);
    onWalletActionResult(true, 'signTx', signedTx)
}


window.walletSignMessage = async function walletSignMessage(message, clientId, chainId) {
    let messageToSign = message
    if(isBase64(message)) {
        console.log('base64 message', message)
        messageToSign = atob(message)
    }
    console.log('signing message', messageToSign)
    setMessage(`Signing message...`)
    await initialiseWallet(clientId, chainId)
    let signer = await window.glipWalletSDK?.getSigner();
    let signedMessage = await signer?.signMessage(messageToSign, true);
    onWalletActionResult(true, 'signMessage', signedMessage)
}



window.walletSignPersonalMessage = async function walletSignPersonalMessage(message, clientId, chainId) {
    let messageToSign = message
    if(isBase64(message)) {
        console.log('base64 message', message)
        messageToSign = atob(message)
    }
    console.log('signing message', messageToSign)
    setMessage(`Signing message...`)
    await initialiseWallet(clientId, chainId)
    let signer = await window.glipWalletSDK?.getSigner();
    let signedMessage = await signer?.signPersonalMessage(messageToSign, true);
    onWalletActionResult(true, 'signPersonalMessage', signedMessage)
}


window.walletSendTx = async function walletSendTx(txData, clientId, chainId) {
    setMessage(`Sending transaction`);
    await initialiseWallet(clientId, chainId)
    let signer = await window.glipWalletSDK?.getSigner();
    let signedTx = await signer?.sendTransaction(JSON.parse(txData));
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

function onWalletActionResult(success, action, result) {
    window.WalletActionInterface.onWalletActionCallback(success, action, result);
}

function setMessage(message) {
    document.getElementById('message').innerHTML = message
}


async function checkWalletAction() {
    const params = new Proxy(new URLSearchParams(window.location.search), {
        get: (searchParams, prop) => searchParams.get(prop),
      });

    let actionType = params.actionType;
    
    console.log('wallet action', actionType)

    switch (actionType) {
        case 'signedMessage':
            onWalletActionResult(
                params.signedMessage && params.signedMessage.length > 0, 'signMessage', params.signedMessage)
            
            break;
        case 'signedTransaction':
            onWalletActionResult(
                params.signedTransaction && params.signedTransaction.length > 0, 'signTransaction', params.signedTransaction)
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
}

checkWalletAction()

function hasUpperCase(str) {
    return str.toLowerCase() != str;
}

function isBase64(str) {
    if (str ==='' || str.trim() ===''){ return false; }
    if(!hasUpperCase(str)){return false}
    try {
        return btoa(atob(str)) == str;
    } catch (err) {
        return false;
    }
}


