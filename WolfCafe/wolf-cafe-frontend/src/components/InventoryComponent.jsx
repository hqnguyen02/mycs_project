import { useEffect, useState } from 'react'
import { getAllItems, updateItem } from '../services/ItemService'

const InventoryComponent = () => {
	
	const [items, setItems] = useState([])
    const [addedAmounts, setAddedAmounts] = useState({})
    const [errors, setErrors] = useState({
        general: "",
        items: {},
    })
    const [sortConfig, setSortConfig] = useState({ key: null, direction: 'ascending' })

    useEffect(() => {
        fetchItems()
    }, [])

    const fetchItems = () => {
        getAllItems().then((response) => {
            console.log("Fetched items:", response.data)
			setItems(response.data)
            const initialAddedAmounts = {}
			if(response.data.length > 0) {
	            response.data.forEach(item => {
	                initialAddedAmounts[item.name] = ''
	            })
			}
            setAddedAmounts(initialAddedAmounts)
        }).catch(error => {
            console.error("Error fetching items:", error)
            setErrors(prevErrors => ({ ...prevErrors, general: "Failed to fetch items" }))
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
    }

    const validateForm = () => {
        let isValid = true
        const newErrors = { 
            general: "",
            items: {}
        }

        Object.entries(addedAmounts).forEach(([name, amount]) => {
            if (amount !== '' && (isNaN(amount) || parseInt(amount) < 0) || amount.indexOf('.') >= 0) {
                newErrors.items[name] = "Must be a non-negative integer"
                isValid = false
            }
        })

        setErrors(newErrors)
        return isValid
    }

	const handleSubmit = (e) => {
        e.preventDefault()

        if (validateForm()) {
            const updatedItems = items.map(item => ({
                ...item,
                inventoryAmount: item.inventoryAmount + (parseInt(addedAmounts[item.name]) || 0)
            }))
			
			updatedItems.forEach((updatedItem) => {
				console.log("Sending updated item:", updatedItem)

				updateItem(updatedItem.id, updatedItem).then((response) => {
				    console.log(response.data)
					if(updatedItem.id == updatedItems[updatedItems.length-1].id) {
						fetchItems()
						setErrors({ general: "", items: {} })
					}
				}).catch(error => {
	                console.error("Error updating item:", error)
	                setErrors(prevErrors => ({ ...prevErrors, general: "Failed to update items" }))
	            })
			})
        }
    }

    const sortedItems = () => {
        if (!items) return []
        
        const sortableItems = [...items]
        if (sortConfig.key !== null) {
            sortableItems.sort((a, b) => {
                if (a[sortConfig.key] < b[sortConfig.key]) {
                    return sortConfig.direction === 'ascending' ? -1 : 1
                }
                if (a[sortConfig.key] > b[sortConfig.key]) {
                    return sortConfig.direction === 'ascending' ? 1 : -1
                }
                return 0
            })
        }
        return sortableItems
    }

    const requestSort = (key) => {
        let direction = 'ascending'
        if (sortConfig.key === key && sortConfig.direction === 'ascending') {
            direction = 'descending'
        }
        setSortConfig({ key, direction })
    }

    if (!items) {
        return <div>Loading...</div>
    }

    return (
        <div className="container">
            <h2 className="text-center mt-5">Inventory</h2>
            {errors.general && <div className="alert alert-danger">{errors.general}</div>}
            <form onSubmit={handleSubmit}>
                <table className="table">
                    <thead>
                        <tr>
                            <th onClick={() => requestSort('name')}>
                                Item {sortConfig.key === 'name' && (sortConfig.direction === 'ascending' ? '▲' : '▼')}
                            </th>
                            <th>
                                Description
                            </th>
							<th>
                                Price
                            </th>
                            <th>Current Amount</th>
                            <th>Add Amount</th>
                        </tr>
                    </thead>
                    <tbody>
                        {sortedItems().map((item) => (
                            <tr key={item.id}>
                                <td>{item.name}</td>
								<td>{item.description}</td>
								<td>{item.price}</td>
                                <td>{item.inventoryAmount}</td>
								<td>
                                    <input
                                        type="number"
                                        value={addedAmounts[item.name]}
                                        onChange={(e) => handleAmountChange(item.name, e.target.value)}
                                        className={`form-control ${errors.items[item.name] ? 'is-invalid' : ''}`}
                                        min="0"
                                    />
                                    {errors.items[item.name] && (
                                        <div className="invalid-feedback">
                                            {errors.items[item.name]}
                                        </div>
                                    )}
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>
                <button type="submit" className="btn btn-primary" onClick={(e) => handleSubmit(e)}>Add Inventory</button>
            </form>
        </div>
    )
}

export default InventoryComponent