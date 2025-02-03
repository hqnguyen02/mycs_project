import React from 'react'
import { useEffect, useState } from 'react'
import { getOrderById, updateOrder, getAllOrders, ORDER_STATUS } from '../services/OrderService'
import { isStaffUser, isCustomerUser, getLoggedInUser } from '../services/AuthService'

const ListOrderComponent = () => {
    const [orders, setOrders] = useState([])
    const [error, setError] = useState('')
    const [loading, setLoading] = useState(true)
    const isStaff = isStaffUser()
    const isCustomer = isCustomerUser()
    const currentUsername = getLoggedInUser()

    useEffect(() => {
        getOrderList()
    }, [])

    function getOrderList() {
        setLoading(true)
        getAllOrders()
            .then((response) => {
                console.log('Raw orders data:', response.data)
                if (!isStaff) {
                    const filteredOrders = response.data.filter(order => order.username === currentUsername)
                    setOrders(filteredOrders)
                } else {
                    setOrders(response.data)
                }
                setError('')
            })
            .catch(error => {
                console.error('Error fetching orders:', error)
                setError('Failed to fetch orders. Please try again.')
            })
            .finally(() => {
                setLoading(false)
            })
    }

    function handleFulfillOrder(orderId) {
        if (!isStaff) {
            setError('Only staff members can update order status.')
            return
        }

        console.log('Attempting to fulfill order:', orderId)
        updateOrder(orderId, ORDER_STATUS.FULFILLING)
            .then((response) => {
                console.log('Order status updated to Fulfilling')
                getOrderList()
            })
            .catch(error => {
                console.error('Error fulfilling order:', error)
                setError('Failed to fulfill order. Please try again.')
            })
    }

    function handleFinishOrder(orderId) {
        if (!isStaff) {
            setError('Only staff members can update order status.')
            return
        }

        console.log('Attempting to finish order:', orderId)
        updateOrder(orderId, ORDER_STATUS.FULFILLED)
            .then((response) => {
                console.log('Order status updated to Fulfilled')
                getOrderList()
            })
            .catch(error => {
                console.error('Error finishing order:', error)
                setError('Failed to finish order. Please try again.')
            })
    }

    function handlePickupOrder(orderId) {
        if (!isCustomer) {
            setError('Only customers can mark orders as picked up.')
            return
        }

        console.log('Attempting to mark order as picked up:', orderId)
        updateOrder(orderId, ORDER_STATUS.PICKED_UP)
            .then((response) => {
                console.log('Order status updated to Picked up')
                getOrderList()
            })
            .catch(error => {
                console.error('Error marking order as picked up:', error)
                setError('Failed to mark order as picked up. Please try again.')
            })
    }

    if (loading) {
        return <div className="container mt-5">Loading orders...</div>
    }

    if (error) {
        return (
            <div className="container mt-5">
                <div className="alert alert-danger">{error}</div>
            </div>
        )
    }

    if (orders.length === 0) {
        return (
            <div className="container mt-5">
                <h2 className="text-center">
                    {isStaff ? 'List of All Orders' : 'Your Orders'}
                </h2>
                <div className="alert alert-info">No orders found.</div>
            </div>
        )
    }

    // Debug logging
    console.log('Current user role:', isCustomer ? 'Customer' : isStaff ? 'Staff' : 'Unknown')
    console.log('Orders:', orders)

    return (
        <div className="container mt-5">
            <h2 className="text-center mb-4">
                {isStaff ? 'List of All Orders' : 'Your Orders'}
            </h2>
            <table className="table table-striped table-bordered">
                <thead className="table-dark">
                    <tr>
                        <th>Order ID</th>
                        <th>Items</th>
                        <th>Status</th>
                        {isStaff && <th>Customer Username</th>}
                        <th>Total Price</th>
                        {(isStaff || isCustomer) && <th>Actions</th>}
                    </tr>
                </thead>
                <tbody>
                    {orders.map(order => (
                        <tr key={order.id}>
                            <td>{order.id}</td>
                            <td>{order.items ? order.items.map(item => item.name).join(', ') : 'No items'}</td>
                            <td>{order.status}</td>
                            {isStaff && <td>{order.username}</td>}
                            <td>${order.totalPrice?.toFixed(2)}</td>
                            {(isStaff || isCustomer) && (
                                <td>
                                    {isStaff && order.status === ORDER_STATUS.ORDERED && (
                                        <button 
                                            className="btn btn-primary btn-sm me-2"
                                            onClick={() => handleFulfillOrder(order.id)}
                                        >
                                            Start Fulfilling
                                        </button>
                                    )}
                                    
                                    {isStaff && order.status === ORDER_STATUS.FULFILLING && (
                                        <button 
                                            className="btn btn-success btn-sm"
                                            onClick={() => handleFinishOrder(order.id)}
                                        >
                                            Mark Fulfilled
                                        </button>
                                    )}
                                    
                                    {isCustomer && order.status === ORDER_STATUS.FULFILLED && (
                                        <button 
                                            className="btn btn-success btn-sm"
                                            onClick={() => handlePickupOrder(order.id)}
                                        >
                                            Mark as Picked Up
                                        </button>
                                    )}
                                    
                                    {((isStaff && !["Ordered", "Fulfilling"].includes(order.status)) ||
                                     (isCustomer && order.status !== ORDER_STATUS.FULFILLED)) && (
                                        <span className="text-muted">No actions available</span>
                                    )}
                                </td>
                            )}
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    )
}

export default ListOrderComponent

