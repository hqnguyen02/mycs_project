import './App.css'
import {BrowserRouter, Routes, Route, Navigate} from 'react-router-dom'

import RegisterStaffComponent from './components/RegisterStaffComponent'
import RegisterManagerComponent from './components/RegisterManagerComponent'
import ListUsersComponent from './components/ListUsersComponent'
import EditUserComponent from './components/EditUserComponent'
import EditStaffComponent from './components/EditStaffComponent'
import HeaderComponent from './components/HeaderComponent'
import FooterComponent from './components/FooterComponent'
import ListItemsComponent from './components/ListItemsComponent'
import ListOrderComponent from './components/ListOrderComponent'
import ItemComponent from './components/ItemComponent'
import InventoryComponent from './components/InventoryComponent'
import RegisterComponent from './components/RegisterComponent'
import LoginComponent from './components/LoginComponent'
import TaxRateComponent from './components/TaxRateComponent'
import EditCustomerComponent from './components/EditCustomerComponent'
import { isUserLoggedIn, isAdminUser, isCustomerUser } from './services/AuthService'
import CreateOrderComponent from './components/CreateOrderComponent'

function App() {
  function AuthenticatedRoute({children}) {
    const isAuth = isUserLoggedIn()
    if (isAuth) {
      return children
    }
    return <Navigate to='/' />
  }

  function AdminRoute({children}) {
    const isAuth = isUserLoggedIn() && isAdminUser()
    if (isAuth) {
      return children
    }
    return <Navigate to='/' />
  }
  
  function CustomerRoute({children}) {
      const isAuth = isUserLoggedIn() && isCustomerUser()
      if (isAuth) {
        return children
      }
      return <Navigate to='/' />
    }

  return (
    <>
      <BrowserRouter>
        <HeaderComponent />
        <Routes>
          <Route path='/' element={<LoginComponent />}></Route>
          <Route path='/register' element={<RegisterComponent />}></Route>
          <Route path='/login' element={<LoginComponent />}></Route>
          <Route path='/items' element={<AuthenticatedRoute><ListItemsComponent /></AuthenticatedRoute>}></Route>
          <Route path='/add-item' element={<AuthenticatedRoute><ItemComponent /></AuthenticatedRoute>}></Route>
		  <Route path='/inventory' element={<AuthenticatedRoute><InventoryComponent /></AuthenticatedRoute>}></Route>
          <Route path='/update-item/:id' element={<AuthenticatedRoute><ItemComponent /></AuthenticatedRoute>}></Route>
		  <Route path='/add-manager' element={<AuthenticatedRoute><RegisterManagerComponent /></AuthenticatedRoute>} />
          <Route path='/add-staff' element={<AuthenticatedRoute><RegisterStaffComponent /></AuthenticatedRoute>} />
		  <Route path='/edit-customer' element={<CustomerRoute><EditCustomerComponent /></CustomerRoute>} />
		  <Route path='/list-users' element={<AuthenticatedRoute><ListUsersComponent /></AuthenticatedRoute>} />
		  <Route path='/edit-user/:userId' element={<AuthenticatedRoute><EditUserComponent /></AuthenticatedRoute>} />
		  <Route path='/edit-staff/:id' element={<AuthenticatedRoute><EditStaffComponent /></AuthenticatedRoute>} />
		  <Route path='/tax-rate' element={<AdminRoute><TaxRateComponent /></AdminRoute>}></Route>
		  <Route path='/view-orders' element={<AuthenticatedRoute><ListOrderComponent /></AuthenticatedRoute>} />
		  <Route path='/order' element={<CustomerRoute><CreateOrderComponent /></CustomerRoute>} />

        </Routes>
        <FooterComponent />
      </BrowserRouter>
    </>
  )
}

export default App
