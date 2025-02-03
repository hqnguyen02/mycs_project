import React, { useEffect, useState } from 'react'
import { getAllUsers, getStaff, deleteUser, deleteStaff } from '../services/AuthService'
import { useNavigate } from 'react-router-dom'
import { isAdminUser, isManagerUser } from '../services/AuthService'

/**
 * Lists all the users in the system, and provides an option to
 * delete or edit them.
 * 
 * This function was based on the listRecipesComponent, implemented as part of CoffeeMaker.
 */
const ListUsersComponent = () => {
	
	const isAdmin = isAdminUser()
	
	const isManager = isManagerUser()

    const [users, setUsers] = useState([])

    const navigator = useNavigate();

    useEffect(() => {
        getUserList()
    }, [])

    function getUserList() {
		if(isAdmin) {
	        getAllUsers().then((response) => {
	        	setUsers(response.data)
	        }).catch(error => {
	            console.error(error)
	        })
		}
		else if(isManager) {
			getStaff().then((response) => {
	        	setUsers(response.data)
	        }).catch(error => {
	            console.error(error)
	        })
		}
    }

    function removeUser(id) {
        console.log(id)

		if(isAdmin) {
	        deleteUser(id).then((response) => {
	            getUserList()
	        }).catch(error => {
	            console.error(error)
	        })
		}
		else if(isManager) {
			deleteStaff(id).then((response) => {
				console.log(response)
	            getUserList()
	        }).catch(error => {
	            console.error(error)
	        })
		}
    }

	function editUser(id) {
	    console.log(id)

		if(isAdmin) {
	    	navigator("/edit-user/" + id)
		}
		else if(isManager) {
			navigator("/edit-staff/" + id.toString())
		}
	}

    return (
        <div className="container">
            <h2 className="text-center">List of Users</h2>
            <table className="table table-striped table-bordered">
                <thead>
                    <tr>
                        <th>Username</th>
                        <th>Email</th>
						<th>Role</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    {
                        users.map(user => 
                        <tr key={user.id}>
                            <td>{user.userName}</td>
							<td>{user.email}</td>
							<td>{user.roles[0]}</td>
                            <td>
                                <button className="btn btn-primary" onClick={() => editUser(user.id)}
                                    style={{marginLeft: '10px'}}
                                >Edit</button>
								<button className="btn btn-danger" onClick={() => removeUser(user.id)}
								    style={{marginLeft: '10px'}}
								>Delete</button>
                            </td>
                        </tr>)
                    }
                </tbody>
            </table>
        </div>
    )

}

export default ListUsersComponent