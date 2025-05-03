export function handleOfflineError(error, cb=undefined) {

    console.log("Check code")
    console.log(error);
    if(404 === error.code) {
        if(cb) {
            console.log("calling back");
            cb();
        }

        console.log("redirecting");
        document.location.href = './offline';
    } else {
        throw error;
    }
}

export function isOffline(error, cb=undefined) {

    console.log("Check code")
    console.log(error.code);
    if(404 === error.code) {
        return true;
    }

    return false;
}