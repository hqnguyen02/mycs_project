package edu.ncsu.csc326.wolfcafe.service;

import java.util.List;

import edu.ncsu.csc326.wolfcafe.dto.ItemDto;
import edu.ncsu.csc326.wolfcafe.dto.OrderDto;

/**
 * Order service
 *
 * @author Shakthi Ravichandran
 */
public interface OrderService {

    /**
     * String for ordered state.
     */
    public final String ORDER_ORDERED    = "Ordered";

    /**
     * String for fulfilling state.
     */
    public final String ORDER_FULFILLING = "Fulfilling";

    /**
     * String for fulfilled state.
     */
    public final String ORDER_FULFILLED  = "Fulfilled";

    /**
     * String for picked up state.
     */
    public final String ORDER_PICKED_UP  = "Picked up";

    /**
     * String for cancelled state.
     */
    public final String ORDER_CANCELLED  = "Cancelled";

    /**
     * Adds items to order.
     *
     * @param orderId
     *            ID of order to add to.
     * @param orderItemDtos
     *            items to add.
     * @return updated OrderDto.
     */
    public OrderDto addItemToOrder ( Long orderId, List<ItemDto> orderItemDtos );

    /**
     * Adds tip to order.
     *
     * @param orderId
     *            ID of order to add tip to.
     * @param tipPercent
     *            percentage of tip.
     * @return updated OrderDto.
     */
    public OrderDto addTipToOrder ( Long orderId, Double tipPercent );

    /**
     * Gets total price of order.
     *
     * @param order
     *            order to get price of.
     * @return double price of order.
     */
    public Double calculateTotalPrice ( OrderDto order );

    /**
     * Gets order using order ID.
     *
     * @param orderId
     *            ID of order.
     * @return relevant OrderDto.
     */
    public OrderDto getOrderById ( Long orderId );

    /**
     * Creates a new order.
     *
     * @param orderDto
     *            data to populate new order with.
     * @return new OrderDto.
     */
    public OrderDto createOrder ( OrderDto orderDto );

    /**
     * Updates order status with the passed string.
     *
     * @param status
     *            the status.
     * @param orderDto
     *            the order to update.
     * @return new OrderDto.
     */
    public OrderDto updateOrder ( String status, OrderDto orderDto );

    /**
     * Returns all orders
     *
     * @return all orders
     */
    List<OrderDto> getAllOrders ();
}
