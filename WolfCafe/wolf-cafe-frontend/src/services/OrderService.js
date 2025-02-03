import axios from 'axios'


const BASE_REST_API_URL = 'http://localhost:8080/api/order'

export const ORDER_STATUS = {
    ORDERED: "Ordered",
    FULFILLING: "Fulfilling",
    FULFILLED: "Fulfilled",
    PICKED_UP: "Picked up",
    CANCELLED: "Cancelled"
}

/** GET Orders - gets all orders */
export const getAllOrders = () => {
    return axios.get(BASE_REST_API_URL);
}

/** GET Order - gets the Order with the given id */
export const getOrderById = (id) => {
    return axios.get(`${BASE_REST_API_URL}/${id}`);
}

/** POST Order - creates the given Order */
export const createOrder = (orderDto) => {
    return axios.post(BASE_REST_API_URL, orderDto);
}

/** PUT Order - updates the order with the given id */
export const updateOrder = (id, status) => {
    console.log('Sending status update for order:', id, 'new status:', status);
    return axios.post(`${BASE_REST_API_URL}/${id}/status`, 
        JSON.stringify(status),
        {
            headers: {
                'Content-Type': 'application/json'
            }
        }
    );
}

