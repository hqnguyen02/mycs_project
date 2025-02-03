package edu.ncsu.csc326.wolfcafe.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc326.wolfcafe.dto.OrderDto;
import edu.ncsu.csc326.wolfcafe.entity.User;
import edu.ncsu.csc326.wolfcafe.repository.UserRepository;
import edu.ncsu.csc326.wolfcafe.service.OrderService;

/**
 * Controller for API endpoints for an Order
 */
@CrossOrigin ( origins = "*", allowedHeaders = "*" )
@RestController
@RequestMapping ( "/api/order" )
public class OrderController {
	/** Connects to orderService to perform backend operations */
    @Autowired
    private OrderService   orderService;

    /** Connection to UserRepository */
    @Autowired
    private UserRepository userRepository;

    /**
     * REST API method to allow for order retrieval.
     *
     * @param id
     *            The valid Order id.
     * @return ResponseEntity indicating success if the Order could be
     *         retrieved, or an error if it could not be
     */
    @GetMapping ( "{id}" )
    public ResponseEntity<OrderDto> getOrder ( @PathVariable ( "id" ) final Long id ) {
        final OrderDto orderDto = orderService.getOrderById( id );
        return ResponseEntity.ok( orderDto );
    }

    /**
     * REST API method to allow for order creation.
     *
     * @param orderDto
     *            The valid Order to be saved.
     * @return ResponseEntity indicating success if the Order could be saved, or
     *         an error if it could not be
     */
    @PreAuthorize ( "hasRole('CUSTOMER')" )
    @PostMapping
    public ResponseEntity<OrderDto> createOrder ( @RequestBody final OrderDto orderDto ) {
        if ( orderDto.getId() < 0 ) {
            return new ResponseEntity<>( orderDto, HttpStatus.BAD_REQUEST );
        }
        final Optional<User> user = userRepository.findByUsername( orderDto.getUsername());
        if ( user.isEmpty() ) {
            return new ResponseEntity<>( orderDto, HttpStatus.BAD_REQUEST );
        }
        if ( orderDto.getTip() < 0 || orderDto.getTotalPrice() < 0 ) {
            return new ResponseEntity<>( orderDto, HttpStatus.BAD_REQUEST );
        }

        final OrderDto newOrderDto = orderService.createOrder( orderDto );
        return ResponseEntity.ok( newOrderDto );
    }

    /**
     * REST API method to allow for order updating
     *
     * @param id
     *            The valid Order id.
     * @param status
     *            the status of the order
     * @return ResponseEntity indicating success if the Order could be updated,
     *         or an error if it could not be
     */
    @PostMapping ( "{id}/status" )
    public ResponseEntity<OrderDto> updateOrder ( @PathVariable ( "id" ) final Long id,
            @RequestBody final String status ) {
        try {
            final OrderDto orderDto = orderService.getOrderById( id );
            if ( orderDto == null ) {
                return new ResponseEntity<>( orderDto, HttpStatus.NOT_FOUND );
            }

            // Remove any quotes from the status string if present
            final String cleanStatus = status.replace( "\"", "" );
            final OrderDto updatedOrderDto = orderService.updateOrder( cleanStatus, orderDto );
            return ResponseEntity.ok( updatedOrderDto );
        }
        catch ( final Exception e ) {
            return new ResponseEntity<>( new OrderDto(), HttpStatus.INTERNAL_SERVER_ERROR );
        }
    }

    /**
     * REST API method to allow for order retrieval of all orders
     *
     * @return ResponseEntity indicating success if the Orders could be
     *         retrieved, or an error if they could not be
     */
    @GetMapping
    public ResponseEntity<List<OrderDto>> getAllOrders () {
        final List<OrderDto> orders = orderService.getAllOrders();
        return ResponseEntity.ok( orders );
    }
}
