import React, { useEffect, useState } from 'react'
import { editCustomer, deleteCustomer, logout, getLoggedInUser } from '../services/AuthService'
import { useNavigate } from 'react-router-dom'


/** 
 * Allows a customer to be edited. Based off EditUserComponent.
 */
const EditCustomerComponent = () => {
	
	const [username, setUsername] = useState("")
	const [email, setEmail] = useState("")
	const [password, setPassword] = useState("")
	const [errors, setErrors] = useState({
		name: "",
		username: "",
		email: "",
		password: "",
		general: ""
	})
	const navigator = useNavigate()

    useEffect(() => {
        getUserInfo()
    }, [])

    function getUserInfo() {
		setUsername(getLoggedInUser())
    }
	
	function allAlphanumeric(str) {
		for (let c of str ) {
			if ( ! ( ( '0' <= c && c <= '9' ) || ( 'a' <= c && c <= 'z' ) || ( 'A' <= c && c <= 'Z' ) ) ) {
			    return false;
			}
		}
		return true;
	}
	
	function validLength(str) {
		let len = str.length
		return 3 <= len  && len <= 255
	}
	
	function validateEmail(str) {
		let lastAt = email.lastIndexOf( '@' );
		let lastPeriod = email.lastIndexOf( '.' );

		// If there is no @ or period, return false
		if ( lastAt == -1 || lastPeriod == -1 ) {
		    return false;
		}

		// Email string must be at least five characters long
		if ( email.length < 5 ) {
		    return false;
		}

		// Check that there is only one @ symbol
		if ( lastAt != email.indexOf( '@' ) ) {
		    return false;
		}

		// @ must be in front of period
		if ( lastPeriod < lastAt ) {
		    return false;
		}

		// period can not be the last character
		if ( lastPeriod == email.length - 1 ) {
		    return false;
		}

		// @ cannot be the first character
		if ( lastAt == 0 ) {
		    return false;
		}

		// There must be at least one character between @ and period
		if ( lastPeriod == lastAt + 1 ) {
		    return false;
		}

		// If all the above checks passed, the email is valid
		return true;
	}
	
	function validateForm() {
		
		document.getElementById('success-message').style = "visibility: hidden;"
		
		let valid = true
		let errCpy = {... errors}
		errCpy.general = ""
		
		if (validLength(username)) {
			errCpy.username = ""
		}
		else {
			errCpy.username = "Name length must be between 3 and 255 characters"
			valid = false
		}

		if (password == "" || validLength(password)) {
			errCpy.password = ""
		}
		else {
			errCpy.password = "password length must be between 3 and 255 characters"
			valid = false
		}

		if (validateEmail(email)) {
			errCpy.email = ""
		}
		else {
			errCpy.email = "Invalid email"
			valid = false
		}

		if (!allAlphanumeric(username)) {
			errCpy.username = "Username must have only numbers and letters"
			valid = false
		}
		
		setErrors(errCpy)
		return valid;
		
	}
	
	function timeout(delay) {
		return new Promise(resolve => setTimeout(resolve, delay))
	}

    function editCustomerInfo() {
		
		if (!validateForm()) {
			return
		}
		
		let userDto = {}
		userDto.username = username
		userDto.email = email
		userDto.password = password
		userDto.role = 'CUSTOMER'
		
		console.log(userDto)
		
		editCustomer(userDto).then(async (response) => {
		    console.log(response)
			document.getElementById('success-message').style = "visibility: visible; color:green;"
			await timeout(3000)
			logout()
		    navigator('/login')	
		}).catch(error => {
		    console.error(error)
			let errCpy = {... errors}
			if (error.response.status == 401) {
				errCpy.general = "This user no longer exists"
			}
			else {
				errCpy.general = error.response.data.message
			}
			setErrors(errCpy);
		})
    }
	
	function getGeneralErrors() {
	    if (errors.general) {
	        return <div className="p-3 mb-2 bg-danger text-white">{errors.general}</div>
	    }
	}
	
	function deleteUser() {
		deleteCustomer().then((response) => {
		    console.log(response)
			logout()
		    navigator('/login')	
		}).catch(error => {
		    console.error(error)
			let errCpy = {... errors}
			if (error.response.status == 401) {
				errCpy.general = "This user no longer exists"
			}
			else {
				errCpy.general = error.response.data.message
			}
			setErrors(errCpy);
		})
	}

	return (
	        <div className="container">
	            <br />
				<div style={{display: "flex", justifyContent: "right"}}>
					<button type="button" className="btn btn-danger"
						style={{"fontSize":'25px'}}
                        onClick={ ( )=> {deleteUser()}}
                    >Delete Account</button>
				</div>
				<br />
	            <div className="row">
	                <div className="card col-md-6 offset-md-3">
	                    <h2 className="text-center">Edit User</h2>
	                    <div className="card-body">
							{ getGeneralErrors() }
	                        <form>
								<div className="form-group mb-2">
								    <label className="form-label">Username</label>
								    <input 
								        type="text"
								        name="userName"
								        placeholder="Enter username"
								        value={username}
								        onChange={(e) => {setUsername(e.target.value)}}
										className={`form-control ${errors.username ? "is-invalid":""}`}
								    >
								    </input>
									{errors.username && <div className="invalid-feedback">{errors.username}</div>}
								</div>
								<div className="form-group mb-2">
								    <label className="form-label">Email</label>
								    <input 
								        type="text"
								        name="email"
								        placeholder="Enter email"
								        value={email}
								        onChange={(e) => {setEmail(e.target.value)}}
										className={`form-control ${errors.email ? "is-invalid":""}`}
								    >
								    </input>
									{errors.email && <div className="invalid-feedback">{errors.email}</div>}
								</div>
								<div className="form-group mb-2">
								    <label className="form-label">Password</label>
								    <input 
								        type="text"
								        name="password"
								        placeholder="Enter password (optional)"
								        value={password}
								        onChange={(e) => {setPassword(e.target.value)}}
										className={`form-control ${errors.password ? "is-invalid":""}`}
								    >
								    </input>
									{errors.password && <div className="invalid-feedback">{errors.password}</div>}
								</div>
								<br />
                                <ul className="center_list">
                                    <li>
                                    	<button type="button" className="btn btn-primary"
                                            style={{"fontSize":'25px'}}
                                            onClick={ ( )=> {editCustomerInfo()}}
                                            >Save Information</button>
                                	</li>
									<div id="success-message" style={{visibility:'hidden'}}>Information successfully updated! You will now be logged out.</div>
                                </ul>
	                        </form>
	                    </div>
	                </div>
	            </div>
	        </div>
	    )

}

export default EditCustomerComponent