package edu.ncsu.csc326.wolfcafe.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.ncsu.csc326.wolfcafe.dto.ItemDto;
import edu.ncsu.csc326.wolfcafe.repository.ItemRepository;
import edu.ncsu.csc326.wolfcafe.service.ItemService;
import jakarta.transaction.Transactional;

/**
 * Test class for ItemController.java
 */
@SpringBootTest
@AutoConfigureMockMvc
public class ItemControllerTest {
	/** Mock MVC for testing controller */
    @Autowired
    private MockMvc                   mvc;
    /** Reference to the item repository */
    @Autowired
    private ItemRepository itemRepository;
    /** Mapper for transitioning between object types */
    private static final ObjectMapper MAPPER           = new ObjectMapper();
    /** API path name */
    private static final String       API_PATH         = "/api/items";
    /** Encoding */
    private static final String       ENCODING         = "utf-8";
    /** Common name of item used in testing */
    private static final String       ITEM_NAME        = "Coffee";
    /** Common description of item used in testing */
    private static final String       ITEM_DESCRIPTION = "Coffee is life";
    /** Common price of item used in testing */
    private static final double       ITEM_PRICE       = 3.25;
    /** ItemService used for testing */
    @Autowired
    private ItemService               itemService;
    
    
    /**
     * Tests ItemController.addItem as a staff member
     * @throws Exception if mvc cannot be performed
     */
    @BeforeEach
    public void setUp () throws Exception {
        itemRepository.deleteAll();
    }

    /** Test method for ItemController.addItem() */
    @Test
    @Transactional
    @WithMockUser ( username = "staff", roles = "STAFF" )
    public void testAddItem () throws Exception {
        // Create final ItemDto with all final contents but the final id
        final ItemDto itemDto = new ItemDto();
        itemDto.setName( ITEM_NAME );
        itemDto.setDescription( ITEM_DESCRIPTION );
        itemDto.setPrice( ITEM_PRICE );
        itemDto.setInventoryAmount( 1 );

        final String json = MAPPER.writeValueAsString( itemDto );

        mvc.perform( post( API_PATH ).contentType( MediaType.APPLICATION_JSON ).characterEncoding( ENCODING )
                .content( json ).accept( MediaType.APPLICATION_JSON ) ).andExpect( status().isCreated() )
                .andExpect( jsonPath( "$.name", Matchers.equalTo( ITEM_NAME ) ) )
                .andExpect( jsonPath( "$.description", Matchers.equalTo( ITEM_DESCRIPTION ) ) )
                .andExpect( jsonPath( "$.price", Matchers.equalTo( ITEM_PRICE ) ) );
    }

    /**
     * Tests ItemController.addItem as a non-staff member
     * @throws Exception if mvc cannot be performed
     */
    @Test
    @Transactional
    public void testCreateItemNotAdmin () throws Exception {
        // Create final ItemDto with all final contents but the final id
        final ItemDto itemDto = new ItemDto();
        itemDto.setName( ITEM_NAME );
        itemDto.setDescription( ITEM_DESCRIPTION );
        itemDto.setPrice( ITEM_PRICE );
        itemDto.setInventoryAmount( 1 );

        final String json = MAPPER.writeValueAsString( itemDto );

        // Set id for the response itemDto.setId(57L);

        mvc.perform( post( API_PATH ).contentType( MediaType.APPLICATION_JSON ).characterEncoding( ENCODING )
                .content( json ).accept( MediaType.APPLICATION_JSON ) ).andExpect( status().isUnauthorized() );
    }

    /**
     * Tests ItemController.getItem() as a Staff member
     * @throws Exception if mvc cannot be performed
     */
    @Test
    @WithMockUser ( username = "staff", roles = "STAFF" )
    public void testGetItemById () throws Exception {
        final ItemDto itemDto = new ItemDto();
        itemDto.setName( ITEM_NAME );
        itemDto.setDescription( ITEM_DESCRIPTION );
        itemDto.setPrice( ITEM_PRICE );
        itemDto.setInventoryAmount( 2 );

        final Long id = itemService.addItem( itemDto ).getId();

        final String json = "";

        mvc.perform( get( API_PATH + "/" + id ).contentType( MediaType.APPLICATION_JSON ).characterEncoding( ENCODING )
                .content( json ).accept( MediaType.APPLICATION_JSON ) ).andExpect( status().isOk() )
                .andExpect( jsonPath( "$.name", Matchers.equalTo( ITEM_NAME ) ) )
                .andExpect( jsonPath( "$.description", Matchers.equalTo( ITEM_DESCRIPTION ) ) )
                .andExpect( jsonPath( "$.price", Matchers.equalTo( ITEM_PRICE ) ) );
    }

    /** Test method to update an item */
    @Test
    @Transactional
    @WithMockUser ( username = "staff", roles = "STAFF" )
    public void testUpdateItem () throws Exception {

        ItemDto itemDto = new ItemDto( 0L, "Item", "Description", 1.00, 1 );
        String json = MAPPER.writeValueAsString( itemDto );

        mvc.perform( post( API_PATH ).contentType( MediaType.APPLICATION_JSON ).characterEncoding( ENCODING )
                .content( json ).accept( MediaType.APPLICATION_JSON ) ).andExpect( status().isCreated() )
                .andExpect( jsonPath( "$.name", Matchers.equalTo( "Item" ) ) )
                .andExpect( jsonPath( "$.description", Matchers.equalTo( "Description" ) ) )
                .andExpect( jsonPath( "$.price", Matchers.equalTo( 1.00 ) ) );

        // Update the item
        itemDto = new ItemDto( 0L, "New name", "New description", 2.00, 1 );
        json = MAPPER.writeValueAsString( itemDto );
        final Long id = itemRepository.findAll().get( 0 ).getId();

        mvc.perform( put( API_PATH + "/" + id ).contentType( MediaType.APPLICATION_JSON ).characterEncoding( ENCODING )
                .content( json ).accept( MediaType.APPLICATION_JSON ) ).andExpect( status().isOk() )
                .andExpect( jsonPath( "$.name", Matchers.equalTo( "New name" ) ) )
                .andExpect( jsonPath( "$.description", Matchers.equalTo( "New description" ) ) )
                .andExpect( jsonPath( "$.price", Matchers.equalTo( 2.00 ) ) );
    }

    /** Test method to delete an item */
    @Test
    @Transactional
    @WithMockUser ( username = "staff", roles = "STAFF" )
    public void testDeleteItem () throws Exception {

        final ItemDto itemDto = new ItemDto( 0L, "Item", "Description", 1.00, 1 );
        final String json = MAPPER.writeValueAsString( itemDto );

        mvc.perform( post( API_PATH ).contentType( MediaType.APPLICATION_JSON ).characterEncoding( ENCODING )
                .content( json ).accept( MediaType.APPLICATION_JSON ) ).andExpect( status().isCreated() )
                .andExpect( jsonPath( "$.name", Matchers.equalTo( "Item" ) ) )
                .andExpect( jsonPath( "$.description", Matchers.equalTo( "Description" ) ) )
                .andExpect( jsonPath( "$.price", Matchers.equalTo( 1.00 ) ) );

        // delete the item
        final Long id = itemRepository.findAll().get( 0 ).getId();
        mvc.perform( delete( API_PATH + "/" + id ).contentType( MediaType.APPLICATION_JSON )
                .characterEncoding( ENCODING ).content( json ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isOk() );

        // The repository should be empty
        assertEquals( 0, itemService.getAllItems().size() );
    }

    /** Test method to get all items */
    @Test
    @Transactional
    @WithMockUser ( username = "staff", roles = "STAFF" )
    public void testGetAllItems () throws Exception {

        ItemDto itemDto = new ItemDto( 0L, "Item", "Description", 1.00, 1 );
        String json = MAPPER.writeValueAsString( itemDto );

        mvc.perform( post( API_PATH ).contentType( MediaType.APPLICATION_JSON ).characterEncoding( ENCODING )
                .content( json ).accept( MediaType.APPLICATION_JSON ) ).andExpect( status().isCreated() )
                .andExpect( jsonPath( "$.name", Matchers.equalTo( "Item" ) ) )
                .andExpect( jsonPath( "$.description", Matchers.equalTo( "Description" ) ) )
                .andExpect( jsonPath( "$.price", Matchers.equalTo( 1.00 ) ) );

        itemDto = new ItemDto( 0L, "Item2", "Description", 1.00, 1 );
        json = MAPPER.writeValueAsString( itemDto );

        mvc.perform( post( API_PATH ).contentType( MediaType.APPLICATION_JSON ).characterEncoding( ENCODING )
                .content( json ).accept( MediaType.APPLICATION_JSON ) ).andExpect( status().isCreated() )
                .andExpect( jsonPath( "$.name", Matchers.equalTo( "Item2" ) ) )
                .andExpect( jsonPath( "$.description", Matchers.equalTo( "Description" ) ) )
                .andExpect( jsonPath( "$.price", Matchers.equalTo( 1.00 ) ) );

        // Get all items
        mvc.perform( get( API_PATH ).contentType( MediaType.APPLICATION_JSON ).characterEncoding( ENCODING )
                .content( json ).accept( MediaType.APPLICATION_JSON ) ).andExpect( status().isOk() )
                .andExpect( jsonPath( "$.[0].name", Matchers.equalTo( "Item" ) ) )
                .andExpect( jsonPath( "$.[1].name", Matchers.equalTo( "Item2" ) ) );
    }
}
