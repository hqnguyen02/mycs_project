import React, { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { isAdminUser, isStaffUser } from '../services/AuthService'
import { getAllItems, deleteItemById } from '../services/ItemService'


const ListItemsComponent = () => {
	
	const [items, setItems] = useState([])

	const navigate = useNavigate()

	const isAdmin = isAdminUser()
	
	const isStaff = isStaffUser()
	
	useEffect(() => {
	    listItems()
	}, [])

	function listItems() {
	    getAllItems().then((response) => {
	        setItems(response.data)
	    }).catch(error => {
	        console.error(error)
	    })
	}
	
	function addNewItem() {
		navigate('/add-item')
	}
	
	function updateItem(id) {
		console.log(id)
		navigate(`/update-item/${id}`)
	}
	
	function deleteItem(id) {
		console.log(id)
		deleteItemById(id).then((response) => {
			listItems()
		}).catch(error => {
			console.error(error)
		})
	}

	
	return (
		<div className='container'>
			<br /> <br />
		    <h2 className='text-center'>Items</h2>
			{
				isStaff && 
				<button className='btn btn-primary mb-2' onClick={addNewItem}>Add Item</button>
			}
			<div>
				<table className='table table-bordered table-striped'>
					<thead>
						<tr>
							<th>Item Name</th>
							<th>Price</th>
							<th>Amount in Inventory</th>
							<th>Actions</th>
						</tr>
					</thead>
					<tbody>
						{
							items.map((item) =>
								<tr key={item.id}>
									<td>{item.name}</td>
									<td>{item.price}</td>
									<td>{item.inventoryAmount}</td>
									<td>
										{
											isStaff && <button className='btn btn-info' onClick={() => updateItem(item.id)}>Update</button>
										}
										{
											isStaff && <button className='btn btn-danger' onClick={() => deleteItem(item.id)}style={{marginLeft: "10px"}}>Delete</button>
										}
									</td>
								</tr>
							)
						}
					</tbody>
				</table>
			</div>
		</div>
	)
}

export default ListItemsComponent