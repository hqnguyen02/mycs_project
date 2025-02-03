import axios from 'axios'

const AUTH_REST_API_BASE_URL = 'http://localhost:8080/api/auth'

export const registerAPICall = (registerObj) => axios.post(AUTH_REST_API_BASE_URL + '/register', registerObj)

export const registerStaffAPICall = (staffRegister) => {
    const token = localStorage.getItem('token');
	console.log(token)
    return axios.post(AUTH_REST_API_BASE_URL + '/register/staff', staffRegister, {
        headers: {
            'Authorization': `${token}`,
        }
    });
};

export const registerManagerAPICall = (managerRegister) => {
    const token = localStorage.getItem('token');
	console.log(token)
    return axios.post(AUTH_REST_API_BASE_URL + '/register/manager', managerRegister, {
        headers: {
            'Authorization': `${token}`,
        }
    });
};

export const loginAPICall = (usernameOrEmail, password) => axios.post(AUTH_REST_API_BASE_URL + '/login', { usernameOrEmail, password })

export const storeToken = (token) => localStorage.setItem('token', token)

export const getToken = () => localStorage.getItem('token')

export const saveLoggedInUser = (username, role) => {
    sessionStorage.setItem('authenticatedUser', username)
    sessionStorage.setItem('role', role)
}

export const isUserLoggedIn = () => {
    const username = sessionStorage.getItem('authenticatedUser')

    if (username == null) return false
    else return true
}

export const getLoggedInUser = () => {
    const username = sessionStorage.getItem('authenticatedUser')
    return username
}

export const logout = () => {
    localStorage.clear()
    sessionStorage.clear()
}

export const isAdminUser = () => {
    let role = sessionStorage.getItem('role')
    return role != null && role == 'ROLE_ADMIN';
}	

export const isStaffUser = () => {
    let role = sessionStorage.getItem('role')
    return role != null && role == 'ROLE_STAFF';
}

export const isManagerUser = () => {
    let role = sessionStorage.getItem('role')
    return role != null && role == 'ROLE_MANAGER';
}

export const isCustomerUser = () => {
    let role = sessionStorage.getItem('role')
    return role != null && role == 'ROLE_CUSTOMER';
}

export const getAllUsers = () => {
	return axios.get(AUTH_REST_API_BASE_URL + '/all-users');
}
	
export const getUser = (id) => {
	return axios.get(AUTH_REST_API_BASE_URL + '/user/' + id);
}

export const getStaffById = (id) => {
	return axios.get(AUTH_REST_API_BASE_URL + '/staff/' + id);
}

export const getStaff = () => {
	return axios.get(AUTH_REST_API_BASE_URL + '/get-staff');
}
	
export const editUser = (id, user) => {
	return axios.put(AUTH_REST_API_BASE_URL + '/user/' + id, user);
}

export const getCustomer = (id) => {
		const idConv = Number(id)
		const token = localStorage.getItem('token');
		return axios.get(AUTH_REST_API_BASE_URL + '/customer/' + idConv, {
		        headers: {
		            'Authorization': `${token}`,
		        }
		    });
	}

export const editCustomer = (customer) => {
		return axios.put(AUTH_REST_API_BASE_URL + '/edit-customer', customer);
	}

export const deleteCustomer = () => {
		return axios.delete(AUTH_REST_API_BASE_URL + '/delete-customer');
	}

export const deleteUser = (id) => {
	return axios.delete(AUTH_REST_API_BASE_URL + '/user/' + id);
}

export const editStaff = (id, user) => {
	return axios.put(AUTH_REST_API_BASE_URL + '/staff/' + id, user);
}

export const deleteStaff = (id) => {
	return axios.delete(AUTH_REST_API_BASE_URL + '/staff/' + id);
}
