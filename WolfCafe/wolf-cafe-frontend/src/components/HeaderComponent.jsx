import React, { useEffect, useState } from 'react'
import { NavLink } from 'react-router-dom'
import { useNavigate } from 'react-router-dom'
import { isUserLoggedIn, logout, isAdminUser, isCustomerUser, isStaffUser, isManagerUser } from '../services/AuthService'

const HeaderComponent = () => {
	
	const isAuth = isUserLoggedIn()
	const navigator = useNavigate()
	const isAdmin = isAdminUser()
	const isStaff = isStaffUser()
	const isManager = isManagerUser()
	const isCustomer = isCustomerUser()

	function handleLogout() {
	    logout()
	    navigator('/login')
	}

  return (
    <div>
        <header>
            <nav className='navbar navbar-expand-md navbar-dark bg-dark'>
                <div>
                    <a href='http://localhost:3000' className='navbar-brand'>
                        WolfCafe
                    </a>
                </div>
				<div className='collapse navbar-collapse'>
					<ul className='navbar-nav'>
					
					</ul>
				</div>
				<ul className='navbar-nav'>
                    {
                        !isAuth && 
                        <li className='nav-item'>
                            <NavLink to='/register' className='nav-link'>Register</NavLink>
                        </li>
                    }
                    {
                        !isAuth &&
                        <li className='nav-item'>
                            <NavLink to='/login' className='nav-link'>Login</NavLink>
                        </li>
                    }
					{
						isAuth &&
						isAdmin &&
		                <li className="nav-item">
		                  <NavLink className="nav-link" to="/add-manager">Add Manager</NavLink>
		                </li>
	              	}
          			{
						isAuth &&
						(isAdmin || isManager) &&
		                <li className="nav-item">
		                  <NavLink className="nav-link" to="/add-staff">Add Staff</NavLink>
		                </li>
	              	}
					{
					    isAuth &&
						(isAdmin || isManager) && 
						<li className="nav-item">
						  <NavLink className="nav-link" to="/list-users">List Users</NavLink>
						</li> 
					}  
					{
					    isAuth && 
						isAdmin && 
						<li className="nav-item">
						  <NavLink className="nav-link" to="/tax-rate">Edit Tax Rate</NavLink>
						</li> 
					}
					{
						//Customer
						isAuth &&
						isCustomer &&
						<li className='nav-item'>
							<NavLink to='/edit-customer' className='nav-link'>Edit Account</NavLink>
						</li>
					} 
					{
	                    // Customer - Create Order
	                    isAuth && isCustomer && 
	                    <li className="nav-item">
	                        <NavLink className="nav-link" to="/order">Create Order</NavLink>
	                    </li>
	                }
					{
						// Staff
						isAuth &&
						isStaff &&
						<li className='nav-item'>
							<NavLink to='/items' className='nav-link'>Add Item</NavLink>
						</li>
					}
					{
						// Staff authorized
						isAuth &&
					    (isStaff ||
						isCustomer) &&
					    <li className='nav-item'>
					    <NavLink to='/view-orders' className='nav-link'>View Orders</NavLink>
					    </li>
					}  
					{
						// Manager
						isAuth &&
						isManager &&
						<li className='nav-item'>
							<NavLink to='/inventory' className='nav-link'>Inventory</NavLink>
						</li>
					} 
					{
						// All authorized
                        isAuth &&
                        <li className='nav-item'>
                            <NavLink to='/login' className='nav-link' onClick={handleLogout}>Logout</NavLink>
                        </li>
                    } 
					
                </ul>
            </nav>
        </header>
    </div>
  )
}



export default HeaderComponent