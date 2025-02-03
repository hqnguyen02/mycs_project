package edu.ncsu.csc326.wolfcafe.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents an item for sale in the WolfCafe.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table ( name = "items" )
public class Item {

	/** Id of item in repository */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /** Item's name */
    @Column ( nullable = false, unique = true )
    private String name;
    /** Description of item */
    private String description;
    /** Price of item */
    @Column(nullable = false)

    private double price;
    /** Amount of item in inventory */
    private int inventoryAmount;


}
