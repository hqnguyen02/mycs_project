import React, { useState } from 'react'
import { registerManagerAPICall } from '../services/AuthService'

const RegisterStaffComponent = () => {
	// Use state hook for username, email, password and error
    const [username, setUsername] = useState("")
    const [email, setEmail] = useState("")
    const [password, setPassword] = useState("")
    const [errors, setErrors] = useState({
        username: "",
        email: "",
        password: "",
        general: ""
    })
	const [successMessage, setSuccessMessage] = useState("");
	
	// Function Declaration for registering a staff
    function handleManagerRegistrationForm(e) {
		e.preventDefault();
		setSuccessMessage("");
		// Call validate form and create manager account
		if (validateForm()) {
	        const managerRegister = { username, email, password };
			registerManagerAPICall(managerRegister)
				.then((response) => {
	            	console.log(response.data);
					setSuccessMessage("The manager was registered successfully!");
					setErrors({
								username: "",
						        email: "",
						        password: "",
						        general: ""
		                 	});
	        })
			// Catch the various error when creating the manager
			.catch(error => {
				const errorsCopy = { ...errors };
                if (error.response.status === 403) {
                    errorsCopy.general = "Only admins can create user with manager role.";
				}
				else if (error.response.status === 400) {
				    errorsCopy.general = "Username/email already exists.";
                } 
				else {
                    errorsCopy.general = "Server error";
                }
                setErrors(errorsCopy);
	        });
		}
    }
	// Function Declaration for validateForm. Validate the user input for username, email and password.
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
	
	const getGeneralErrors = () => {
        if (errors.general) {
            return <div className="p-3 mb-2 bg-danger text-white">{errors.general}</div>;
        }
    };
	
	const getSuccessMessage = () => {
        if (successMessage) {
            return <div className="p-3 mb-2 bg-success text-white">{successMessage}</div>;
        }
    };
	
  // Create the form or user interface for registering a manager account
  return (
    <div className='container'>
        <br /><br />
        <div className='row'>
            <div className='col-md-6 offset-md-3 offset-md-3'>
                <div className='card'>
                    <div className='card-header'>
                        <h2 className='text-center'>Manager Registration Form</h2>
                    </div>
                    <div className='card-body'>
						{getGeneralErrors()}
						{getSuccessMessage()}
                        <form>               
                            <div className='row mb-3'>
                                <label className='col-md-3 control-label'>Username</label>
                                <div className='col-md-9'>
                                    <input
                                        type='text'
                                        name='username'
                                        className='form-control'
                                        placeholder='Enter username'
                                        value={username}
                                        onChange={(e) => setUsername(e.target.value)}
                                    >
                                    </input>
                                </div>
                            </div>

                            <div className='row mb-3'>
                                <label className='col-md-3 control-label'>Email</label>
                                <div className='col-md-9'>
                                    <input
                                        type='text'
                                        name='email'
                                        className='form-control'
                                        placeholder='Enter email'
                                        value={email}
                                        onChange={(e) => setEmail(e.target.value)}
                                    >
                                    </input>
                                </div>
                            </div>

                            <div className='row mb-3'>
                                <label className='col-md-3 control-label'>Password</label>
                                <div className='col-md-9'>
                                    <input
                                        type='password'
                                        name='password'
                                        className='form-control'
                                        placeholder='Enter password'
                                        value={password}
                                        onChange={(e) => setPassword(e.target.value)}
                                    >
                                    </input>
                                </div>
                            </div>

                            <div className='form-group mb-3'>
                                <button className='btn btn-primary' onClick={(e) => handleManagerRegistrationForm(e)}>Submit</button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>
  )
}

export default RegisterStaffComponent