/**
 * Test suite for the ItemService class
 *
 * @author Krisjian Smith
 */
package edu.ncsu.csc326.wolfcafe.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import edu.ncsu.csc326.wolfcafe.dto.ItemDto;
import edu.ncsu.csc326.wolfcafe.entity.Item;
import edu.ncsu.csc326.wolfcafe.exception.ResourceNotFoundException;
import edu.ncsu.csc326.wolfcafe.exception.WolfCafeAPIException;
import edu.ncsu.csc326.wolfcafe.repository.ItemRepository;

/**
 * Tests InventoryServiceImpl.
=======

import edu.ncsu.csc326.wolfcafe.dto.ItemDto;
import edu.ncsu.csc326.wolfcafe.entity.Item;
import edu.ncsu.csc326.wolfcafe.exception.ResourceNotFoundException;
import edu.ncsu.csc326.wolfcafe.exception.WolfCafeAPIException;
import edu.ncsu.csc326.wolfcafe.repository.ItemRepository;
import jakarta.transaction.Transactional;

/**
 * Tests IngredientService class
>>>>>>> ksmith33/implement-UC1
 */
@SpringBootTest
public class ItemServiceTest {


    /** ItemSrevice used in each test */
    @Autowired
    private ItemService    itemService;

    /** ItemRepository used for testing */
    @Autowired
    private ItemRepository itemRepository;

    /** The tolerance allowed for asserting that two doubles are equal */
    private final double   DELTA = 0.0001;

    /** Sets up test case by removing all items */
    @BeforeEach
    public void setUp () throws Exception {
        itemRepository.deleteAll();
    }

    /**
     * Test method for ItemService.addItem()
     */
    @Test
    @Transactional
    public void testAddItem () {

        // Add a new item
        final ItemDto item = itemService.addItem( new ItemDto( 0L, "Item", "This is an item", 1.50, 1 ) );

        // Make sure that the item was added to the repository
        final Item retrievedItem = itemRepository.findById( item.getId() )
                .orElseThrow( () -> new ResourceNotFoundException( "No item exists" ) );

        assertEquals( "Item", retrievedItem.getName() );
        assertEquals( "This is an item", retrievedItem.getDescription() );
        assertEquals( 1.50, retrievedItem.getPrice(), DELTA );

        // Try to make another item of the same name
        assertThrows( WolfCafeAPIException.class,
                () -> itemService.addItem( new ItemDto( 1L, "Item", "Another item", 2.00, 1 ) ) );

        // Try invalid items
        assertThrows( WolfCafeAPIException.class,
                () -> itemService.addItem( new ItemDto( 1L, "", "Another item", 2.00, 1 ) ) );
        assertThrows( WolfCafeAPIException.class,
                () -> itemService.addItem( new ItemDto( 1L, "abc", "Another item", 0.0, 1 ) ) );
        assertThrows( WolfCafeAPIException.class,
                () -> itemService.addItem( new ItemDto( 1L, "def", "Another item", 2.00, -1 ) ) );
    }

    /**
     * Test method for ItemService.getItem() and ItemService.getAllItems()
     */
    @Test
    @Transactional
    public void testGetItems () {

        // Add some items
        final ItemDto item1 = itemService.addItem( new ItemDto( 0L, "Item 1", "This is item 1", 1.50, 1 ) );
        itemService.addItem( new ItemDto( 1L, "Item 2", "This is item 2", 2.01, 1 ) );
        itemService.addItem( new ItemDto( 2L, "Item 3", "This is item 3", 3.99, 1 ) );
        final ItemDto item4 = itemService.addItem( new ItemDto( 3L, "Item 4", "This is item 4", 4.00, 1 ) );
        itemService.addItem( new ItemDto( 4L, "Item 5", "This is item 5", 5.90, 1 ) );

        // Get some items
        ItemDto retrievedItem = itemService.getItem( item1.getId() );
        assertEquals( item1.getName(), retrievedItem.getName() );
        assertEquals( item1.getDescription(), retrievedItem.getDescription() );
        assertEquals( item1.getPrice(), retrievedItem.getPrice() );

        retrievedItem = itemService.getItem( item4.getId() );
        assertEquals( item4.getName(), retrievedItem.getName() );
        assertEquals( item4.getDescription(), retrievedItem.getDescription() );
        assertEquals( item4.getPrice(), retrievedItem.getPrice() );

        // Get all items
        final List<ItemDto> items = itemService.getAllItems();
        assertEquals( 5, items.size() );

        // Try to get item that doesn't exist
        assertThrows( ResourceNotFoundException.class, () -> itemService.getItem( 0L ) );
    }

    /**
     * Test method for ItemService.updateItem()
     */
    @Test
    @Transactional
    public void testUpdateItem () {

        // Make an item
        final ItemDto item1 = itemService.addItem( new ItemDto( 0L, "Item 1", "This is item 1", 1.50, 1 ) );
        assertEquals( "Item 1", itemService.getItem( item1.getId() ).getName() );

        // Update that item
        itemService.updateItem( item1.getId(), new ItemDto( 2L, "New Name", "New Description", 1.00, 1 ) );
        ItemDto retrievedItem = itemService.getItem( item1.getId() );
        assertEquals( "New Name", retrievedItem.getName() );
        assertEquals( "New Description", retrievedItem.getDescription() );
        assertEquals( 1.00, retrievedItem.getPrice(), DELTA );

        // Make sure there is only one item in the system
        assertEquals( 1, itemService.getAllItems().size() );

        // Try to edit item that doesn't exist
        assertThrows( ResourceNotFoundException.class, () -> itemService.updateItem( item1.getId() + 1, item1 ) );

        // Try to edit an item, but keep the same name
        itemService.updateItem( item1.getId(), new ItemDto( 0L, "New Name", "abc", 0.01, 1 ) );
        retrievedItem = itemService.getItem( item1.getId() );
        assertEquals( "New Name", retrievedItem.getName() );
        assertEquals( "abc", retrievedItem.getDescription() );
        assertEquals( 0.01, retrievedItem.getPrice(), DELTA );

    }

    /**
     * Test method for ItemService.deleteItem()
     */
    @Test
    @Transactional
    public void testDeleteItem () {

        // Make an item
        final ItemDto item1 = itemService.addItem( new ItemDto( 0L, "Item 1", "This is item 1", 1.50, 1 ) );
        assertEquals( "Item 1", itemService.getItem( item1.getId() ).getName() );

        // Delete that item
        itemService.deleteItem( item1.getId() );

        // Check that there are no items in the system
        assertEquals( 0, itemService.getAllItems().size() );

        // Try to delete item that doesn't exist
        assertThrows( ResourceNotFoundException.class, () -> itemService.deleteItem( 1L ) );
    }

}
