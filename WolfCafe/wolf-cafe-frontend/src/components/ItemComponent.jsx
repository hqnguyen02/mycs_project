import React from 'react'
import { useEffect, useState } from 'react'
import { getItemById, saveItem, updateItem } from '../services/ItemService'
import { useNavigate, useParams } from 'react-router-dom'

// Returns true if the given string only contains digits
// if float is true, then a period is also allowed
function isNumber(str, float) {
	str = str.toString()
	
	// Empty strings are not numbers
	if (str.length == 0) {
		return false;
	}
	
	// Iterate over each character
	let foundPeriod = false;
	for (let c of str) {
		
		// If the character is a period, return false
		// only if we are not looking for a float or if
		// we've already found a period
		if (c == '.') {
			
			if (foundPeriod) {
				return false;
			}
			
			if (!float) {
				return false;
			}
			foundPeriod = true;
		}
		
		// If the letter is not between 0 and 9, return false
		else if (!('0' <= c && c <= '9')) {
			return false;
		}
	}
	return true;
}

const TodoComponent = () => {

    const [name, setName] = useState('')
    const [description, setDescription] = useState('') // Currently unused
	const [price, setPrice] = useState('')
	const [amount, setAmount] = useState('');
    const { id } = useParams()
	const [errors, setErrors] = useState({
	    general: "",
	    itemName: "",
	    itemPrice: "",
		itemAmount: ""
	})

    const navigate = useNavigate()

    useEffect(() => {
        if(id) {
            getItemById(id).then((response) => {
                console.log(response.data)
                setName(response.data.name)
                setDescription(response.data.description)
				setPrice(response.data.price)
				setAmount(response.data.amount)
            }).catch(error => {
                console.error(error)
            })
        }
    }, [id])

    function saveOrUpdateItem(e) {
        e.preventDefault()
		let description = ""
        const item = {name, description, price, inventoryAmount: amount}
        console.log(item)
		const errorsCopy = 		{
			    general: "",
			    itemName: "",
			    itemPrice: "",
				itemAmount: ""
			}
		// Make sure item price is valid
		let valid = true
		if (!isNumber(price, true) || parseInt(price) <= 0) {
			valid = false;
			errorsCopy.itemPrice = "Price must be a number greater than 0"
		}
		
		if (!isNumber(amount, false) || parseFloat(amount) <= 0) {
			valid = false;
			errorsCopy.itemAmount = "Amount must be an integer greater than 0"
		}
		
		if (name == "") {
			valid = false;
			errorsCopy.itemName = "Item name cannot be empty"
		}
		
		if (!valid) {
			setErrors(errorsCopy);
			console.log(errors)
			return;
		}

        if (id) {
            updateItem(id, item).then((response) => {
                console.log(response.data)
                navigate('/items')
            }).catch(error => {
                console.error(error)
				console.log(error.response.data)
				if (error.response.data.message === "Item with the given name already exists") {
					errorsCopy.itemName = "Item with this name already exists"
					setErrors(errorsCopy)
					return
				}
            })
        } else {
            saveItem(item).then((response) => {
                console.log(response.data)
                navigate('/items')
            }).catch(error => {
                console.error(error)
				console.log(error.response.data)
				if (error.response.data.message === "Item with the given name already exists") {
					errorsCopy.itemName = "Item with this name already exists"
					setErrors(errorsCopy)
					return
				}
            })
        }
    }

    function pageTitle() {
        if (id) {
            return <h2 className='text-center'>Update Item</h2>
        } else {
            return <h2 className='text-center'>Add Item</h2>
        }
    }

  return (
    <div className='container'>
        <br /> <br />
        <div className='row'>
            <div className='card col-md-6 offset-md-3 offset-md-3'>
                { pageTitle() }
                
                <div className='card-body'>
                    <form>
                        <div className='form-group mb-2'>
                            <label className='form-label'>Name</label>
                            <input 
                                type='text'
                                className={`form-control ${errors.itemName ? "is-invalid":""}`}
                                placeholder='Enter Item Name'
                                name='name'
                                value={name}
                                onChange={(e) => setName(e.target.value)}
                            >
                            </input>
							{errors.itemName && <div className="invalid-feedback">{errors.itemName}</div>}
                        </div>

                        <div className='form-group mb-2'>
                            <label className='form-label'>Price</label>
                            <input 
                                type='text'
								className={`form-control ${errors.itemPrice ? "is-invalid":""}`}
                                placeholder='Enter Item Price'
                                name='price'
                                value={price}
                                onChange={(e) => setPrice(e.target.value)}
                            >
                            </input>
							{errors.itemPrice && <div className="invalid-feedback">{errors.itemPrice}</div>}
                        </div>

                        <div className='form-group mb-2'>
                            <label className='form-label'>Amount</label>
							<input 
                                type='text'
								className={`form-control ${errors.itemAmount ? "is-invalid":""}`}
                                placeholder='Enter Item Amount'
                                name='amount'
                                value={amount}
                                onChange={(e) => setAmount(e.target.value)}
                            >
                            </input>
							{errors.itemAmount && <div className="invalid-feedback">{errors.itemAmount}</div>}
                        </div>

                        <button type='submit' className='btn btn-success' onClick={(e) => saveOrUpdateItem(e)}>Submit</button>
                    </form>
                </div>
            </div>
        </div>
    </div>
  )
}

export default TodoComponent