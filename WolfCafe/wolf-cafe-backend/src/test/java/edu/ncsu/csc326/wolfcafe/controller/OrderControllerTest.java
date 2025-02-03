/**
 * Order Controller Class tests
 */
package edu.ncsu.csc326.wolfcafe.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import edu.ncsu.csc326.wolfcafe.TestUtils;
import edu.ncsu.csc326.wolfcafe.dto.ItemDto;
import edu.ncsu.csc326.wolfcafe.dto.OrderDto;
import edu.ncsu.csc326.wolfcafe.dto.RegisterDto;
import edu.ncsu.csc326.wolfcafe.entity.Order;
import edu.ncsu.csc326.wolfcafe.repository.OrderRepository;
import edu.ncsu.csc326.wolfcafe.repository.UserRepository;

/**
 * Tests the Order Controller class
 */
@SpringBootTest
@AutoConfigureMockMvc
public class OrderControllerTest {

    /** Mock MVC for testing controller */
    @Autowired
    private MockMvc         mvc;

    /** Reference to order repository */
    @Autowired
    private OrderRepository orderRepository;

    /** Reference to User repository */
    @Autowired
    private UserRepository  userRepository;

    /**
     * Sets up the test case.
     *
     * @throws java.lang.Exception
     *             if error
     */
    @BeforeEach
    public void setUp () throws Exception {
        userRepository.deleteAll();
        orderRepository.deleteAll();
    }

    /**
     * Tests createOrder
     *
     * @throws Exception
     *             with invalid call
     */
    @Test
    @WithMockUser ( username = "user", roles = { "CUSTOMER" } )
    @Transactional
    public void testCreateOrder () throws Exception {
        final RegisterDto registerDto = new RegisterDto( "jestes", "vitae.erat@yahoo.edu", "JXB16TBD4LC", "Customer" );

        mvc.perform( post( "/api/auth/register" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( registerDto ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isCreated() ).andExpect( content().string( "User registered successfully." ) );

        final List<ItemDto> list = new ArrayList<ItemDto>();
        final OrderDto o1 = new OrderDto( 0L, "jestes", list, 0.5, 30.0, "Ordered" );

        mvc.perform( post( "/api/order" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( o1 ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isOk() ).andExpect( jsonPath( "$.username" ).value( "jestes" ) )
                .andExpect( jsonPath( "$.status" ).value( "Ordered" ) );
    }

    /**
     * Tests getOrder
     *
     * @throws Exception
     *             with invalid call
     */
    @Test
    @WithMockUser ( username = "user", roles = { "CUSTOMER" } )
    @Transactional
    public void testGetOrder () throws Exception {
        final RegisterDto registerDto = new RegisterDto( "jestes", "vitae.erat@yahoo.edu", "JXB16TBD4LC", "Customer" );

        mvc.perform( post( "/api/auth/register" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( registerDto ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isCreated() ).andExpect( content().string( "User registered successfully." ) );

        final List<ItemDto> list = new ArrayList<ItemDto>();
        final OrderDto o1 = new OrderDto( 0L, "jestes", list, 0.5, 30.0, "Ordered" );

        mvc.perform( post( "/api/order" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( o1 ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isOk() ).andExpect( jsonPath( "$.username" ).value( "jestes" ) )
                .andExpect( jsonPath( "$.status" ).value( "Ordered" ) );

        final List<Order> orders = orderRepository
                .findOrdersByUsername( "jestes" );
        final Long orderId = orders.get( 0 ).getId();

        mvc.perform( get( "/api/order/" + orderId.toString() ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( o1 ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isOk() ).andExpect( jsonPath( "$.username" ).value( "jestes" ) )
                .andExpect( jsonPath( "$.status" ).value( "Ordered" ) );

        mvc.perform( get( "/api/order" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( o1 ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isOk() );
    }

    /**
     * Tests updateOrder
     *
     * @throws Exception
     *             with invalid call
     */
    @Test
    @WithMockUser ( username = "user", roles = { "CUSTOMER" } )
    @Transactional
    public void testUpdateOrder () throws Exception {
        final RegisterDto registerDto = new RegisterDto( "jestes", "vitae.erat@yahoo.edu", "JXB16TBD4LC", "Customer" );

        mvc.perform( post( "/api/auth/register" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( registerDto ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isCreated() ).andExpect( content().string( "User registered successfully." ) );

        final List<ItemDto> list = new ArrayList<ItemDto>();
        final OrderDto o1 = new OrderDto( 0L, "jestes", list, 0.5, 30.0, "Ordered" );

        mvc.perform( post( "/api/order" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( o1 ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isOk() ).andExpect( jsonPath( "$.username" ).value( "jestes" ) )
                .andExpect( jsonPath( "$.status" ).value( "Ordered" ) );

        final List<Order> orders = orderRepository
                .findOrdersByUsername( "jestes" );
        final Long orderId = orders.get( 0 ).getId();

        mvc.perform( get( "/api/order/" + orderId.toString() ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( o1 ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isOk() ).andExpect( jsonPath( "$.status" ).value( "Ordered" ) );

        mvc.perform( post( "/api/order/" + orderId.toString() + "/status" ).contentType( MediaType.APPLICATION_JSON )
                .content( "Fulfilling" ).accept( MediaType.APPLICATION_JSON ) ).andExpect( status().isOk() )
                .andExpect( jsonPath( "$.status" ).value( "Fulfilling" ) );

        mvc.perform( post( "/api/order/" + orderId.toString() + "/status" ).contentType( MediaType.APPLICATION_JSON )
                .content( "Fulfilled" ).accept( MediaType.APPLICATION_JSON ) ).andExpect( status().isOk() )
                .andExpect( jsonPath( "$.status" ).value( "Fulfilled" ) );

        mvc.perform( post( "/api/order/" + orderId.toString() + "/status" ).contentType( MediaType.APPLICATION_JSON )
                .content( "Picked up" ).accept( MediaType.APPLICATION_JSON ) ).andExpect( status().isOk() )
                .andExpect( jsonPath( "$.status" ).value( "Picked up" ) );

        mvc.perform( post( "/api/order/" + orderId.toString() + "/status" ).contentType( MediaType.APPLICATION_JSON )
                .content( "Cancelled" ).accept( MediaType.APPLICATION_JSON ) ).andExpect( status().isOk() )
                .andExpect( jsonPath( "$.status" ).value( "Cancelled" ) );
    }
}
