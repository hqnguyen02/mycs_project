package edu.ncsu.csc326.wolfcafe.mapper;


import edu.ncsu.csc326.wolfcafe.dto.ItemDto;

import edu.ncsu.csc326.wolfcafe.entity.Item;

/**
 * Converts between ItemDto and Item entity

 */
public class ItemMapper {

    /**
     * Converts an Item entity to ItemDto
     *
     * @param item
     *            Item to convert
     * @return ItemDto object
     */
    public static ItemDto mapToItemDto ( Item item ) {

        ItemDto itemDto = new ItemDto( item.getId(), item.getName(), item.getDescription(), item.getPrice(), item.getInventoryAmount());

        return itemDto;
    }

    /**
     * Converts an ItemDto object to an Item entity.
     *
     * @param itemDto
     *            ItemDto to convert
     * @return Item entity
     */
    public static Item mapToItem ( ItemDto itemDto ) {

        Item item = new Item( itemDto.getId(), itemDto.getName(), itemDto.getDescription(), itemDto.getPrice(), itemDto.getInventoryAmount() );

        return item;
    }

}
