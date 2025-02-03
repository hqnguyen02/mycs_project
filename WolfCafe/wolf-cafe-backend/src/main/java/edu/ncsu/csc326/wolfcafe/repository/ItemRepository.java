package edu.ncsu.csc326.wolfcafe.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.ncsu.csc326.wolfcafe.entity.Item;

/**
 * Repository interface for Items.
 */
public interface ItemRepository extends JpaRepository<Item, Long> {

    /**
     * Returns true if an item with the given name exists in the repository
     *
     * @param name
     *            the name to check
     * @return true if an item with the given name exists
     */
    boolean existsByName ( String name );
    
    /**
     * Returns Item with the given name if it exists
     *
     * @param name
     *            the name to search for
     * @return Item if an item with the given name exists
     */
    Optional<Item> findByName ( String name );
}
