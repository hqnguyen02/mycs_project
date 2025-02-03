package edu.ncsu.csc326.wolfcafe.mapper;

import java.util.ArrayList;
import java.util.List;

import edu.ncsu.csc326.wolfcafe.dto.ItemDto;
import edu.ncsu.csc326.wolfcafe.dto.OrderDto;
import edu.ncsu.csc326.wolfcafe.entity.Item;
import edu.ncsu.csc326.wolfcafe.entity.Order;

/**
 * Converts between OrderDto and Order entity
 * @author Shakthi Ravichandran
 */
public class OrderMapper {

    /**
     * Converts an Order entity to OrderDto
     *
     * @param order
     *            Order to convert
     * @return OrderDto object
     */
    public static OrderDto mapToOrderDto ( Order order ) {
        OrderDto orderDto = new OrderDto();
        orderDto.setId( order.getId() );
        orderDto.setUsername( order.getUsername() );
        List<ItemDto> itemDtos = new ArrayList<ItemDto>();
        for ( Item item : order.getItems() ) {
            itemDtos.add( ItemMapper.mapToItemDto( item ) );
        }
        orderDto.setItems( itemDtos );
        orderDto.setTip( order.getTip() );
        orderDto.setTotalPrice( order.getTotalPrice() );
        orderDto.setStatus( order.getStatus() );
        return orderDto;
    }

    /**
     * Converts an OrderDto object to an Order entity.
     *
     * @param orderDto
     *            OrderDto to convert
     * @return Order entity
     */
    public static Order mapToOrder ( OrderDto orderDto ) {
        Order order = new Order();
        order.setId( orderDto.getId() );
        order.setUsername( orderDto.getUsername() );
        List<Item> items = new ArrayList<Item>();
        for ( ItemDto itemDto : orderDto.getItems() ) {
            items.add( ItemMapper.mapToItem( itemDto ) );
        }
        order.setItems( items );
        order.setTip( orderDto.getTip() );
        order.setTotalPrice( orderDto.getTotalPrice() );
        order.setStatus( orderDto.getStatus() );
        return order;
    }

}
