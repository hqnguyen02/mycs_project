package edu.ncsu.csc326.wolfcafe.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import edu.ncsu.csc326.wolfcafe.dto.ItemDto;
import edu.ncsu.csc326.wolfcafe.dto.OrderDto;
import edu.ncsu.csc326.wolfcafe.entity.Item;
import edu.ncsu.csc326.wolfcafe.entity.Order;
import edu.ncsu.csc326.wolfcafe.exception.ResourceNotFoundException;
import edu.ncsu.csc326.wolfcafe.exception.WolfCafeAPIException;
import edu.ncsu.csc326.wolfcafe.mapper.ItemMapper;
import edu.ncsu.csc326.wolfcafe.mapper.OrderMapper;
import edu.ncsu.csc326.wolfcafe.repository.ItemRepository;
import edu.ncsu.csc326.wolfcafe.repository.OrderRepository;
import edu.ncsu.csc326.wolfcafe.service.ItemService;
import edu.ncsu.csc326.wolfcafe.service.OrderService;
import lombok.AllArgsConstructor;

/**
 * Implemented order service
 *
 * @author Shakthi Ravichandran
 * @author Varsha Ravi
 * @author Cate Shepard
 */
@Service
@AllArgsConstructor
public class OrderServiceImpl implements OrderService {

    /**
     * Order repository to be used for saving changes.
     */
    private final OrderRepository orderRepository;
    
    /**
     * Item repository to be used for updating inventory.
     */
    private final ItemRepository itemRepository;
    
    /**
     * Item service to be used for updating inventory.
     */
    private final ItemService itemService;

    @Override
    public OrderDto addItemToOrder ( final Long orderId, final List<ItemDto> itemDtos ) {
        final Order order = orderRepository.findById( orderId )
                .orElseThrow( () -> new ResourceNotFoundException( "Order not found with id " + orderId ) );
        final List<Item> updated = order.getItems();

        for ( int i = 0; i < itemDtos.size(); i++ ) {
            updated.add( ItemMapper.mapToItem( itemDtos.get( i ) ) );
            final Double unrounded = order.getTotalPrice() + itemDtos.get( i ).getPrice();
            order.setTotalPrice( Math.round( unrounded * 100.0 ) / 100.0 );
        }

        order.setItems( updated );
        final Order savedOrder = orderRepository.save( order );
        return OrderMapper.mapToOrderDto( savedOrder );
    }

    @Override
    public OrderDto addTipToOrder ( final Long orderId, final Double tip ) {
        final Order order = orderRepository.findById( orderId )
                .orElseThrow( () -> new ResourceNotFoundException( "Order not found with id " + orderId ) );
        order.setTip( Math.round( tip * order.getTotalPrice() * 100.0 ) / 100.0 );
        final Order savedOrder = orderRepository.save( order );
        return OrderMapper.mapToOrderDto( savedOrder );
    }

    @Override
    public Double calculateTotalPrice ( final OrderDto order ) {
        return order.getTotalPrice() + order.getTip();
    }

    @Override
    public OrderDto getOrderById ( final Long orderId ) {
        final Order order = orderRepository.findById( orderId )
                .orElseThrow( () -> new ResourceNotFoundException( "Order not found with id " + orderId ) );
        return OrderMapper.mapToOrderDto( order );
    }

    @Override
    public OrderDto createOrder ( final OrderDto orderDto ) {
    	// Remove the ordered items from inventory
    	for(ItemDto orderedItem : orderDto.getItems()) {
    		Item item = itemRepository.findByName(orderedItem.getName()).orElseThrow( 
    				() -> new ResourceNotFoundException("Item does not exist with name " + orderedItem.getName()));
    		ItemDto updatedItem = ItemMapper.mapToItemDto(item);
    		if (item.getInventoryAmount() - orderedItem.getInventoryAmount() < 0) {
    			throw new WolfCafeAPIException(HttpStatus.BAD_REQUEST, "Not enough inventory to fulfill order.");
    		}
			updatedItem.setInventoryAmount(item.getInventoryAmount() - orderedItem.getInventoryAmount());
			itemService.updateItem(item.getId(), updatedItem);
    	}
        final Order order = OrderMapper.mapToOrder( orderDto );
        final Order savedOrder = orderRepository.save( order );
        return OrderMapper.mapToOrderDto( savedOrder );
    }

    @Override
    public OrderDto updateOrder ( final String status, final OrderDto orderDto ) {
        final Order order = OrderMapper.mapToOrder( orderDto );
        if ( isValidStatus( status ) ) {
            order.setStatus( status );
            final Order savedOrder = orderRepository.save( order );
            return OrderMapper.mapToOrderDto( savedOrder );
        }
        throw new IllegalArgumentException( "Invalid status." );
    }

    private boolean isValidStatus ( final String status ) {
        return ( status.equals( ORDER_ORDERED ) || status.equals( ORDER_FULFILLING ) || status.equals( ORDER_FULFILLED )
                || status.equals( ORDER_PICKED_UP ) || status.equals( ORDER_CANCELLED ) );
    }

    /**
     * Returns all orders
     *
     * @return all orders
     */
    @Override
    public List<OrderDto> getAllOrders () {
        final List<Order> orders = orderRepository.findAll();
        return orders.stream().map( ( order ) -> OrderMapper.mapToOrderDto( order ) )
                .collect( Collectors.toList() );
    }
}
