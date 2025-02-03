import axios from 'axios'
import { getToken } from './AuthService'

const BASE_REST_API_URL = 'http://localhost:8080/api/tax-rate'

axios.interceptors.request.use(function (config) {
  config.headers['Authorization'] = getToken()
  return config;
}, function (error) {
  return Promise.reject(error);
});

export const getCurrentTaxRate = () => axios.get(BASE_REST_API_URL);

// Updated to match backend's expected format
export const updateTaxRate = (newRate) => {
    return axios.put(BASE_REST_API_URL, {
        id: 1,
        rate: newRate
    });
}