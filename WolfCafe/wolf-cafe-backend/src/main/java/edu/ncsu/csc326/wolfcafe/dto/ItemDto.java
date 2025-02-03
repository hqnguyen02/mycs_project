package edu.ncsu.csc326.wolfcafe.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Item for data transfer.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {
	/** Id of item in repository */
    private Long id;
    /** Item's name */
    private String name;
    /** Description of item */
    private String description;

    /** Price of item */
    private double price;
    /** Amount of item in inventory */
    private int inventoryAmount;

}
