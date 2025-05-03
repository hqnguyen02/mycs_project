export default {
    get: (url) => {
        const promiseResponse = fetch(url);
        return promiseResponse.then(async response => {
            if(!response.ok) {

                const err = new Error();
                const body = await response.json();

                if(body.error) {
                    err.error = body.error;
                }

                err.code = response.status;
                throw err;
            }

            return response.json();
        });
    },
    put: (url, body) => {
        const promiseResponse = fetch(url, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(body)
        });
        return promiseResponse.then(async response => {
            if(!response.ok) {

                const err = new Error();
                const body = await response.json();

                if(body.error) {
                    err.error = body.error;
                }

                err.code = response.status;
                throw err;
            }

            return response.json();
        });
    },
    post: (url, body) => {
        const promiseResponse = fetch(url, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(body)
        });
        return promiseResponse.then(async response => {
            if(!response.ok) {

                const err = new Error();
                const body = await response.json();

                if(body.error) {
                    err.error = body.error;
                }

                err.code = response.status;
                throw err;
            }

            return response.json();
        });
    }
}