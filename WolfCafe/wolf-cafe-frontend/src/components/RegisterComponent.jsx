import React, { useState } from 'react'
import { registerAPICall, loginAPICall, saveLoggedInUser, storeToken  } from '../services/AuthService'
import { useNavigate } from 'react-router-dom'

/** Form to register a new customer and log them in. */
const RegisterComponent = () => {

    const [username, setUsername] = useState('')
    const [email, setEmail] = useState('')
    const [password, setPassword] = useState('')
	
    const navigator = useNavigate()
    const [errors, setErrors] = useState({
        general: "",
        username: "",
        email: "",
        password: "",
    })

	/* Uses registerAPICall from AuthService to attempt to create the customer and log them in */
    function handleRegistrationForm(e) {
        e.preventDefault();
		
		if(validateForm()) {
			// Need name for the RegisterDto, but it's unused, so just leave an empty string.
			const name = ""
			const role = "Customer"
	        const register = { name, username, email, password, role}
	
	        console.log(register)
	
	        registerAPICall(register).then(async (response) => {
	            console.log(response.data)

		        await loginAPICall(username, password).then((response) => {
		            console.log(response.data)

		            const token = 'Bearer ' + response.data.accessToken

		            const role = response.data.role

		            storeToken(token)
		            saveLoggedInUser(username, role)

		            navigator('/items')

		            window.location.reload(false)
		        }).catch(error => {
		            console.error(error)
		        })
			}).catch(error => {
				console.error(error)
				const errorsCopy = {... errors}
                if (error.response.status == 400) {
                    errorsCopy.general = "Username or email has been taken."
                } 
				setErrors(errorsCopy)				
			})
		}
    }
	
	/* Validates the form and prints specific errors on the form if the input is invalid. */
	function validateForm() {
		let valid = true
		
		const errorsCopy = {... errors}

        if (username.trim()) {
			if (username.length < 3 || username.length > 255) {
            	errorsCopy.username = "Username must be between 3 and 255 characters."
				valid = false
			}
			else {
            	errorsCopy.username = ""
			}
        } else {
            errorsCopy.username = "Username is required."
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
		
		if (email.trim()) {
			if (email.length > 255) {
            	errorsCopy.email = "Email must be less than 256 characters."
				valid = false
			}
			else if (email.indexOf("@") < 1) {
				errorsCopy.email = "Invalid email."
				valid = false
			}
			else if (!email.indexOf(".") > email.indexOf("@")) {
				errorsCopy.email = "Invalid email."
				valid = false
			}
			else {
				errorsCopy.email = ""
			}
        } else {
            errorsCopy.email = "Email is required."
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
                        <h2 className='text-center'>User Registration Form</h2>
                    </div>
                    <div className='card-body'>
						{ getGeneralErrors() }
                        <form>

                            <div className='row mb-3'>
                                <label className='col-md-3 control-label'>Username</label>
                                <div className='col-md-9'>
                                    <input
                                        type='text'
                                        name='username'
                                        className={`form-control ${errors.username ? "is-invalid":""}`}
                                        placeholder='Enter username'
                                        value={username}
                                        onChange={(e) => setUsername(e.target.value)}
                                    >
                                    </input>
									{errors.username && <div className="invalid-feedback">{errors.username}</div>}
                                </div>
                            </div>

                            <div className='row mb-3'>
                                <label className='col-md-3 control-label'>Email</label>
                                <div className='col-md-9'>
                                    <input
                                        type='text'
                                        name='email'
                                        className={`form-control ${errors.email ? "is-invalid":""}`}
                                        placeholder='Enter email'
                                        value={email}
                                        onChange={(e) => setEmail(e.target.value)}
                                    >
                                    </input>
									{errors.email && <div className="invalid-feedback">{errors.email}</div>}
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
                                <button className='btn btn-primary' onClick={(e) => handleRegistrationForm(e)}>Submit</button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>
  )
}

export default RegisterComponent