package edu.ncsu.csc326.wolfcafe.repository;

import edu.ncsu.csc326.wolfcafe.entity.Order;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for Items.
 */
public interface OrderRepository extends JpaRepository<Order, Long> {
	/**
	 * Returns the list of orders made by the customer with the given username
	 * @param username username of customer who created the order
	 * @return list of orders created by the user with the given username
	 */
	public List<Order> findOrdersByUsername(String username);
	
	/** Returns Order with the given id if it exists
	 * @param id id of Order to find
	 * @return order with given id if it exists
	 */
	public Optional<Order> findById(Long id);
}
