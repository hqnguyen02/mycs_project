import React, { useEffect, useState } from 'react';
import { getAllItems } from '../services/ItemService';
import { createOrder } from '../services/OrderService';
import {getLoggedInUser} from '../services/AuthService'
import { getCurrentTaxRate } from '../services/TaxRateService';

/**
 * Create an order with one or many items.
 */
const CreateOrderComponent = () => {
	const [items, setItems] = useState([])
	const [addedAmounts, setAddedAmounts] = useState({})
	const [preTaxPrice, setPreTaxPrice] = useState([])
	const [totalPrice, setTotalPrice] = useState([])
	const [tip, setTip] = useState([])
	const [tax, setTax] = useState([])
	const [customDisabled, setCustomDisabled] = useState([])
	const [errors, setErrors] = useState({
        general: "",
        items: {},
    })

    // Fetch items when the component mounts.
	useEffect(() => {
	    listItems()
		setPreTaxPrice(0)
		setTotalPrice(0)
		setTip(1.2)
		setCustomDisabled(true)
		
		getCurrentTaxRate().then(response => {
			setTax(parseFloat(response.data.rate) / 100)
		}).catch(error => {
			console.log(error)
		})
	}, [])
	
	function listItems() {
	    getAllItems().then((response) => {
	        setItems(response.data)
			const initialAddedAmounts = {}
			if(response.data.length > 0) {
	            response.data.forEach(item => {
	                initialAddedAmounts[item.name] = ''
	            })
			}
            setAddedAmounts(initialAddedAmounts)
	    }).catch(error => {
	        console.error(error)
	    })
	}
	
	const handleAmountChange = (itemName, value) => {
        setAddedAmounts({ ...addedAmounts, [itemName]: value })
        // Clear the error for this ingredient when the user starts typing
        setErrors(prevErrors => ({
            ...prevErrors,
            items: {
                ...prevErrors.items,
                [itemName]: ''
            }
        }))
				
		let tempPreTax = 0
		
		items.forEach((item) => {
			if(item.name == itemName) {
				tempPreTax += parseInt(value) * parseFloat(item.price)
			}
			else {
				tempPreTax += (parseInt(addedAmounts[item.name]) || 0) * parseFloat(item.price)
			}
		})
				
		setPreTaxPrice(tempPreTax)
		
		let total = tempPreTax * tax
		total += tempPreTax * tip
		total = Math.round(total * 100) / 100
		setTotalPrice(total)
    }
	
	const handleTipChange = (disableCustom, updatedTip) => {
		setTip(updatedTip)
		
		if(disableCustom) {
			setCustomDisabled(true)
		}
		
		let tempPreTax = 0
				
		items.forEach((item) => {
			tempPreTax += (parseInt(addedAmounts[item.name]) || 0) * parseFloat(item.price)
		})
				
		setPreTaxPrice(tempPreTax)
		
		let total = tempPreTax * tax
		total += tempPreTax * updatedTip
		total = Math.round(total * 100) / 100
		setTotalPrice(total)
	}
	
	function isInvalid() {
		
		if (tip < 0) {
			alert("Your tip cannot be negative.")
			return true
		}
		
		return false;
	}

    // Placeholder for order placement logic
    const handlePlaceOrder = () => {
		if (isInvalid()) {
			return 
		}
		let order = {}
		order.id = 0
		order.username = getLoggedInUser()
		order.items = []
		order.tip = tip;
		order.totalPrice = totalPrice;
		order.status = "Ordered";
		
		for (let i of items) {
			let amt = addedAmounts[i.name]
			if (amt > 0) {
				let newItem = {... i}
				newItem.inventoryAmount = amt
				order.items.push(newItem)
			}
		}
		
		if (order.items.length == 0) {
					
			alert("You must select at least one item!")
			return;
		}
		
		console.log(order)
		
		createOrder(order).then((response) => {
			alert("Successfully created order!")
			}
		).catch((error) => {
			alert("Order could not be created.")
			console.error(error.response)
		})	
    };

	return (
			<div className='container'>
			  <div>
				<br />
				<h2 className='text-center'>Create Order</h2>
				<table style={{display: "flex", justifySelf: "center"}}>
					<tbody>
					<tr>
						<th>
							<div className='card'>
							    <h2 className='text-center'>Items</h2>
								<div>
									<table className='table table-bordered table-striped'>
										<thead>
											<tr>
												<th>Item Name</th>
												<th>Price</th>
												<th>Amount to Order</th>
											</tr>
										</thead>
										<tbody>
											{
												items.map((item) =>
													<tr key={item.id}>
														<td>{item.name}</td>
														<td>{item.price}</td>
														<td>
															<input
							                                        type="number"
							                                        value={addedAmounts[item.name]}
							                                        onChange={(e) => handleAmountChange(item.name, e.target.value)}
							                                        className={`form-control ${errors.items[item.name] ? 'is-invalid' : ''}`}
							                                        min="0"
							                                  />
														</td>
													</tr>
												)
											}
										</tbody>
									</table>
								</div>
							</div>
						</th>
						<th style={{padding: "70px"}}></th>
						<th>
							<div className='card'>
								<h2 className='text-center'>Payment</h2>
								<br />
								<p>Total (Pre Tax): ${preTaxPrice}</p>
								<p> Tip: </p>
									<label><input type="radio" name="tip" value=".15" id="tip15" onChange={() => handleTipChange(true, 1.15)}/> 15%</label>
									<label><input type="radio" name="tip" value=".20" id="tip20" defaultChecked="checked" onChange={() => handleTipChange(true, 1.2)}/> 20%</label>
									<label><input type="radio" name="tip" value=".25" id="tip25" onChange={() => handleTipChange(true, 1.25)}/> 25%</label>
									<label><input type="radio" name="tip" value="custom" id="tipCustom" onChange={() => setCustomDisabled(false)}/> 
											<input type="number" id="customBox" min="0" max="100" placeholder="Custom Tip" defaultValue="0" disabled={customDisabled} onChange={(e) => handleTipChange(false, 1 + e.target.value / 100)}/>%
									</label>
								<br />
								<p> Payment due (incl tax): ${totalPrice} </p>
								<button type="submit" className="btn btn-success" onClick={(e) => handlePlaceOrder(e)}>Checkout</button>
							</div>
						</th>
					</tr>
					</tbody>
				</table>
			  </div>
			</div>
		)
	}

	export default CreateOrderComponent