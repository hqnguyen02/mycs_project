package edu.ncsu.csc326.wolfcafe.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Item for data transfer for the Order.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {
	/** Id of order in the database */
    private Long id;
    /** Username of customer who created the order */
    private String username;
    /** List of items in the order */
    private List<ItemDto> items;
    /** Tip on the order */
    private Double tip;
    /** Total price paid by the customer */
    private Double totalPrice;
    /** Order's current status */
    private String status;
}
