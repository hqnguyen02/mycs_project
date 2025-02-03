package edu.ncsu.csc326.wolfcafe.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import edu.ncsu.csc326.wolfcafe.dto.ItemDto;
import edu.ncsu.csc326.wolfcafe.entity.Item;
import edu.ncsu.csc326.wolfcafe.exception.ResourceNotFoundException;
import edu.ncsu.csc326.wolfcafe.exception.WolfCafeAPIException;
import edu.ncsu.csc326.wolfcafe.mapper.ItemMapper;
import edu.ncsu.csc326.wolfcafe.repository.ItemRepository;
import edu.ncsu.csc326.wolfcafe.service.ItemService;
import lombok.AllArgsConstructor;

/**
 * Implemented item service
 */
@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {

	/** Connection to the item repository to work with the DAO + database */
    private final ItemRepository itemRepository;
    
    /**
     * Adds given item
     *
     * @param itemDto
     *            item to add
     * @return added item
     */
    @Override
    public ItemDto addItem ( final ItemDto itemDto ) {

    	checkItemDto( itemDto );
        checkName( itemDto.getName() );
        
        // Check that no item with the given name exists
        final Item item = ItemMapper.mapToItem(itemDto);
        final Item savedItem = itemRepository.save( item );
        return ItemMapper.mapToItemDto( savedItem );
    }

    /**
     * Gets item by id
     *
     * @param id
     *            id of item to get
     * @return returned item
     */
    @Override
    public ItemDto getItem ( final Long id ) {
        final Item item = itemRepository.findById( id )
                .orElseThrow( () -> new ResourceNotFoundException( "Item not found with id " + id ) );
        return ItemMapper.mapToItemDto( item );
    }

    /**
     * Returns all items
     *
     * @return all items
     */
    @Override
    public List<ItemDto> getAllItems () {
        final List<Item> items = itemRepository.findAll();
        return items.stream().map( ( item ) -> ItemMapper.mapToItemDto( item ) ).collect( Collectors.toList() );
    }

    /**
     * Updates the item with the given id
     *
     * @param id
     *            id of item to update
     * @param itemDto
     *            information of item to update
     * @return updated item
     */
    @Override
    public ItemDto updateItem ( final Long id, final ItemDto itemDto ) {

        // Get the item to update
        final Item item = itemRepository.findById( id )
                .orElseThrow( () -> new ResourceNotFoundException( "Item not found with id " + id ) );

        // If the item is being renamed, check that no other item has that name
        if ( !itemDto.getName().equals( item.getName() ) ) {
            checkName( itemDto.getName() );
        }
        checkItemDto( itemDto );

        item.setName( itemDto.getName() );
        item.setDescription( itemDto.getDescription() );
        item.setPrice( itemDto.getPrice() );
        item.setInventoryAmount( itemDto.getInventoryAmount() );

        final Item updatedItem = itemRepository.save( item );

        return ItemMapper.mapToItemDto( updatedItem );
    }

    /**
     * Deletes the item with the given id
     *
     * @param id
     *            id of item to delete
     */
    @Override
    public void deleteItem ( final Long id ) {
        itemRepository.findById( id )
                .orElseThrow( () -> new ResourceNotFoundException( "Item not found with id " + id ) );
        itemRepository.deleteById( id );
    }

    /**
     * Throws an exception if an item with the given name exists
     *
     * @param name
     *            the name to check for
     * @throws WolfCafeAPIException
     *             if an item with the given name exists
     */
    private void checkName ( final String name ) {
        if ( itemRepository.existsByName( name ) ) {
            throw new WolfCafeAPIException( HttpStatus.BAD_REQUEST, "Item with the given name already exists" );
        }
    }

    /**
     * Checks if the itemDto has a valid name, price, and amount
     *
     * @param itemDto
     *            the itemDto to check
     * @throws WolfCafeAPIException
     *             if the given itemDto is not valid
     */
    private void checkItemDto ( final ItemDto itemDto ) {

        // Check that the item has a valid price and amount
        if ( itemDto.getPrice() <= 0.0 ) {
            throw new WolfCafeAPIException( HttpStatus.BAD_REQUEST, "Price must be greater than 0" );
        }

        if ( itemDto.getInventoryAmount() < 0 ) {
            throw new WolfCafeAPIException( HttpStatus.BAD_REQUEST, "Amount must be positive" );
        }

        // Check that the name is not an empty string
        if ( itemDto.getName().length() == 0 ) {
            throw new WolfCafeAPIException( HttpStatus.BAD_REQUEST, "Name cannot be empty" );
        }
    }
}
