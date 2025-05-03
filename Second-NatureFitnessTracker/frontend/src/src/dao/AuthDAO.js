import HTTPClient from "./HTTPClient";
const BASE_API_PATH = '/api';


export default {
    logIn: (username, password) => {
        const data = {
          username: username,
          password: password
        };
        return HTTPClient.post(`${BASE_API_PATH}/signin`, data);
    },
    signUp: (username, password, email) => {
        const data = {
            username: username,
            password: password,
            email: email
        };
        return HTTPClient.post(`${BASE_API_PATH}/signup`, data);
    },
    logOut: () => {
        return HTTPClient.post(`${BASE_API_PATH}/logout`, {});
    },
    
    getCurrentUser: () => {
        return HTTPClient.get(`${BASE_API_PATH}/users/current`);
    }
}