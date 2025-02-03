import React, { useState, useEffect } from 'react'
import { loginAPICall, saveLoggedInUser, storeToken } from '../services/AuthService'
import { useNavigate } from 'react-router-dom'

/** Form for a user to log in. */
const LoginComponent = () => {

    const [usernameOrEmail, setUsernameOrEmail] = useState('')
    const [password, setPassword] = useState('')

    const navigator = useNavigate()
	const [errors, setErrors] = useState({
        general: "",
        usernameOrEmail: "",
        password: "",
    })
	
	useEffect(() => {
	    saveLoggedInUser(null, null)
		storeToken(null)
	}, [])

	/* Uses loginAPICall from AuthService to attempt to log the customer in */
    async function handleLoginForm(e) {
        e.preventDefault()
		
		if (validateForm()) {
	        const loginObj = {usernameOrEmail, password}
	
	        console.log(loginObj)
	
	        await loginAPICall(usernameOrEmail, password).then((response) => {
	            console.log(response.data)
	
	            const token = 'Bearer ' + response.data.accessToken
	
	            const role = response.data.role
	
	            storeToken(token)
	            saveLoggedInUser(usernameOrEmail, role)
	
				if( role == "ROLE_CUSTOMER" || role == "ROLE_STAFF" ) {
	            	navigator('/view-orders')
				}
				else if (role == "ROLE_ADMIN") {
					navigator('/tax-rate')
				}
				else if (role == "ROLE_MANAGER") {
					navigator('/inventory')
				}
	
	            window.location.reload(false)
	        }).catch(error => {
	            console.error(error)
				const errorsCopy = {... errors}
                if (error.response.status == 401) {
                    errorsCopy.general = "Invalid username or password."
                } 
				setErrors(errorsCopy)	
	        })
		}
    }
	
	/* Validates the form and prints specific errors on the form if the input is invalid. */
	function validateForm() {
		let valid = true
		
		const errorsCopy = {... errors}

        if (usernameOrEmail.trim()) {
			if (usernameOrEmail.length < 3 || usernameOrEmail.length > 255) {
            	errorsCopy.usernameOrEmail = "Username must be between 3 and 255 characters."
				valid = false
			}
			else {
            	errorsCopy.usernameOrEmail = ""
			}
        } else {
            errorsCopy.usernameOrEmail = "Username is required."
            valid = false
        }
		
		if (password.trim()) {
			if (password.length < 3 || password.length > 255) {
            	errorsCopy.password = "Password must be between 3 and 255 characters."
				valid = false
			}
			else {
				errorsCopy.password = ""
			}
        } else {
            errorsCopy.password = "Password is required."
            valid = false
        }
		
		setErrors(errorsCopy)
		
		return valid
	}
		
	/* Checks for general errors and creates a red text box at the top of the form with a specific message for each error. */
	function getGeneralErrors() {
	    if (errors.general) {
	        return <div className="p-3 mb-2 bg-danger text-white">{errors.general}</div>
	    }
	}


  return (
    <div className='container'>
        <br /><br />
        <div className='row'>
            <div className='col-md-6 offset-md-3 offset-md-3'>
                <div className='card'>
                    <div className='card-header'>
                        <h2 className='text-center'>Login Form</h2>
                    </div>
                    <div className='card-body'>
						{ getGeneralErrors() }
                        <form>
                            <div className='row mb-3'>
                                <label className='col-md-3 control-label'>Username</label>
                                <div className='col-md-9'>
                                    <input
                                        type='text'
                                        name='usernameOrEmail'
                                        className={`form-control ${errors.usernameOrEmail ? "is-invalid":""}`}
                                        placeholder='Enter username or email'
                                        value={usernameOrEmail}
                                        onChange={(e) => setUsernameOrEmail(e.target.value)}
                                    >
                                    </input>
									{errors.usernameOrEmail && <div className="invalid-feedback">{errors.usernameOrEmail}</div>}
                                </div>
                            </div>

                            <div className='row mb-3'>
                                <label className='col-md-3 control-label'>Password</label>
                                <div className='col-md-9'>
                                    <input
                                        type='password'
                                        name='password'
                                        className={`form-control ${errors.password ? "is-invalid":""}`}
                                        placeholder='Enter password'
                                        value={password}
                                        onChange={(e) => setPassword(e.target.value)}
                                    >
                                    </input>
									{errors.password && <div className="invalid-feedback">{errors.password}</div>}
                                </div>
                            </div>

                            <div className='form-group mb-3'>
                                <button className='btn btn-primary' onClick={(e) => handleLoginForm(e)}>Submit</button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>
  )
}

export default LoginComponent