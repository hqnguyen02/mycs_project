package edu.ncsu.csc326.wolfcafe.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

import edu.ncsu.csc326.wolfcafe.dto.ItemDto;
import edu.ncsu.csc326.wolfcafe.dto.OrderDto;
import edu.ncsu.csc326.wolfcafe.repository.OrderRepository;

/**
 * Tests Order Service class
 */
@SpringBootTest
class OrderServiceTest {

    /** Reference to item service */
    @Autowired
    private ItemService     itemService;

    /** Reference to order service */
    @Autowired
    private OrderService    orderService;

    /** Reference to order repository */
    @Autowired
    private OrderRepository orderRepository;

    /**
     * Sets up the test case.
     *
     * @throws java.lang.Exception
     *             if error
     */
    @BeforeEach
    public void setUp () throws Exception {
        orderRepository.deleteAll();
    }

    /**
     * Tests addItemToOrder() method.
     */
    @Test
    @WithMockUser
    @Transactional
    void testAddItemToOrder () {
        final List<ItemDto> items = new ArrayList<ItemDto>();
        final ItemDto i1 = new ItemDto( 0L, "item1", "desc1", 0.3, 10 );
        final ItemDto item1 = itemService.addItem( i1 );
        final ItemDto i2 = new ItemDto( 0L, "item2", "desc2", 0.7, 10 );
        final ItemDto item2 = itemService.addItem( i2 );
        final ItemDto i3 = new ItemDto( 0L, "item3", "desc3", 0.5, 10 );
        final ItemDto item3 = itemService.addItem( i3 );
        final ItemDto i4 = new ItemDto( 0L, "item4", "desc4", 1.2, 10 );
        final ItemDto item4 = itemService.addItem( i4 );
        
        final ItemDto orderItem1 = new ItemDto( item1.getId(), "item1", "desc1", 0.3, 5 );
        final ItemDto orderItem2 = new ItemDto( item2.getId(), "item2", "desc2", 0.7, 7 );

        items.add( orderItem1 );
        items.add( orderItem2 );

        final OrderDto o1 = new OrderDto( 0L, "someuser", items, 0.5, 30.0, "status" );

        final OrderDto savedOrder = orderService.createOrder( o1 );

        assertEquals( savedOrder.getItems().size(), 2 );

        final List<ItemDto> items2 = new ArrayList<ItemDto>();
        
        final ItemDto orderItem3 = new ItemDto( item3.getId(), "item3", "desc3", 0.5, 2 );
        final ItemDto orderItem4 = new ItemDto( item4.getId(), "item4", "desc4", 1.2, 8 );

        items2.add( orderItem3 );
        items2.add( orderItem4 );

        final OrderDto newOrder = orderService.addItemToOrder( savedOrder.getId(), items2 );

        assertEquals( newOrder.getItems().size(), 4 );
        assertEquals( newOrder.getTotalPrice(), 31.7 );

        items2.add( orderItem2 );

        orderService.addItemToOrder( savedOrder.getId(), items2 );

        assertEquals( orderRepository.findById( savedOrder.getId() ).get().getItems().size(), 7 );
        assertEquals( orderRepository.findById( savedOrder.getId() ).get().getTotalPrice(), 34.1 );
    }

    /**
     * Tests addTipToOrder() method.
     */
    @Test
    @WithMockUser
    @Transactional
    void testAddTipToOrder () {
        final List<ItemDto> items = new ArrayList<ItemDto>();
        final ItemDto i1 = new ItemDto( 0L, "item1", "desc1", 0.3, 10 );
        final ItemDto item1 = itemService.addItem( i1 );
        final ItemDto i2 = new ItemDto( 0L, "item2", "desc2", 0.7, 10 );
        final ItemDto item2 = itemService.addItem( i2 );
        
        final ItemDto orderItem1 = new ItemDto( item1.getId(), "item1", "desc1", 0.3, 5 );
        final ItemDto orderItem2 = new ItemDto( item2.getId(), "item2", "desc2", 0.7, 7 );
        
        items.add( orderItem1 );
        items.add( orderItem2 );

        final OrderDto o1 = new OrderDto( 0L, "someuser", items, 0.5, 30.0, "status" );
        final OrderDto savedOrder = orderService.createOrder( o1 );

        orderService.addTipToOrder( savedOrder.getId(), 0.1 );

        assertEquals( orderRepository.findById( savedOrder.getId() ).get().getTip(), 3 );

        orderService.addTipToOrder( savedOrder.getId(), 0.05 );

        assertEquals( orderRepository.findById( savedOrder.getId() ).get().getTip(), 1.5 );

        orderService.addTipToOrder( savedOrder.getId(), 0.25 );

        assertEquals( orderRepository.findById( savedOrder.getId() ).get().getTip(), 7.5 );
    }

    /**
     * Tests calculateTotalPrice() method.
     */
    @Test
    @WithMockUser
    @Transactional
    void testCalculateTotalPrice () {
        final List<ItemDto> items = new ArrayList<ItemDto>();
        final ItemDto i1 = new ItemDto( 0L, "item1", "desc1", 0.3, 10 );
        final ItemDto item1 = itemService.addItem( i1 );
        final ItemDto i2 = new ItemDto( 0L, "item2", "desc2", 0.7, 10 );
        final ItemDto item2 = itemService.addItem( i2 );

        final ItemDto orderItem1 = new ItemDto( item1.getId(), "item1", "desc1", 0.3, 5 );
        final ItemDto orderItem2 = new ItemDto( item2.getId(), "item2", "desc2", 0.7, 7 );
        
        items.add( orderItem1 );
        items.add( orderItem2 );

        final OrderDto o1 = new OrderDto( 0L, "someuser", items, 0.5, 30.0, "status" );
        final OrderDto savedOrder = orderService.createOrder( o1 );

        final Double price = orderService.calculateTotalPrice( savedOrder );
        assertEquals( price, 30.5 );
    }

    /**
     * Tests getOrderById() method.
     */
    @Test
    @WithMockUser
    @Transactional
    void testGetOrderById () {
        final List<ItemDto> items = new ArrayList<ItemDto>();
        final ItemDto i1 = new ItemDto( 0L, "item1", "desc1", 0.3, 10 );
        final ItemDto item1 = itemService.addItem( i1 );
        final ItemDto i2 = new ItemDto( 0L, "item2", "desc2", 0.7, 10 );
        final ItemDto item2 = itemService.addItem( i2 );

        final ItemDto orderItem1 = new ItemDto( item1.getId(), "item1", "desc1", 0.3, 2 );
        final ItemDto orderItem2 = new ItemDto( item2.getId(), "item2", "desc2", 0.7, 3 );
        
        items.add( orderItem1 );
        items.add( orderItem2 );

        final OrderDto o1 = new OrderDto( 0L, "someuser", items, 0.5, 30.0, "status" );
        final OrderDto savedOrder1 = orderService.createOrder( o1 );
        final OrderDto o2 = new OrderDto( 1L, "someuser2", items, 0.7, 31.0, "status" );
        final OrderDto savedOrder2 = orderService.createOrder( o2 );

        final OrderDto order1 = orderService.getOrderById( savedOrder1.getId() );
        final OrderDto order2 = orderService.getOrderById( savedOrder2.getId() );

        assertEquals( order1.getId(), savedOrder1.getId() );
        assertEquals( order1.getStatus(), savedOrder1.getStatus() );
        assertEquals( order1.getTip(), savedOrder1.getTip() );
        assertEquals( order1.getTotalPrice(), savedOrder1.getTotalPrice() );
        assertEquals( order1.getUsername(), savedOrder1.getUsername() );

        assertEquals( order2.getId(), savedOrder2.getId() );
        assertEquals( order2.getStatus(), savedOrder2.getStatus() );
        assertEquals( order2.getTip(), savedOrder2.getTip() );
        assertEquals( order2.getTotalPrice(), savedOrder2.getTotalPrice() );
        assertEquals( order2.getUsername(), savedOrder2.getUsername() );
    }

    /**
     * Tests updateOrder() method.
     */
    @Test
    @WithMockUser
    @Transactional
    void testUpdateOrder () {
        final List<ItemDto> items = new ArrayList<ItemDto>();
        final ItemDto i1 = new ItemDto( 0L, "item1", "desc1", 0.3, 10 );
        final ItemDto item1 = itemService.addItem( i1 );
        final ItemDto i2 = new ItemDto( 0L, "item2", "desc2", 0.7, 10 );
        final ItemDto item2 = itemService.addItem( i2 );

        final ItemDto orderItem1 = new ItemDto( item1.getId(), "item1", "desc1", 0.3, 5 );
        final ItemDto orderItem2 = new ItemDto( item2.getId(), "item2", "desc2", 0.7, 10 );
        
        items.add( orderItem1 );
        items.add( orderItem2 );

        final OrderDto o1 = new OrderDto( 0L, "someuser", items, 0.5, 30.0, "status" );
        final OrderDto savedOrder1 = orderService.createOrder( o1 );

        orderService.updateOrder( "Ordered", savedOrder1 );
        assertEquals( orderRepository.findById( savedOrder1.getId() ).get().getStatus(), OrderService.ORDER_ORDERED );

        orderService.updateOrder( "Fulfilling", savedOrder1 );
        assertEquals( orderRepository.findById( savedOrder1.getId() ).get().getStatus(),
                OrderService.ORDER_FULFILLING );

        orderService.updateOrder( "Fulfilled", savedOrder1 );
        assertEquals( orderRepository.findById( savedOrder1.getId() ).get().getStatus(), OrderService.ORDER_FULFILLED );

        orderService.updateOrder( "Picked up", savedOrder1 );
        assertEquals( orderRepository.findById( savedOrder1.getId() ).get().getStatus(), OrderService.ORDER_PICKED_UP );

        orderService.updateOrder( "Cancelled", savedOrder1 );
        assertEquals( orderRepository.findById( savedOrder1.getId() ).get().getStatus(), OrderService.ORDER_CANCELLED );
    }

}
