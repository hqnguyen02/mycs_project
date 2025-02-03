package edu.ncsu.csc326.wolfcafe.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import edu.ncsu.csc326.wolfcafe.entity.TaxRate;
import edu.ncsu.csc326.wolfcafe.repository.TaxRateRepository;

/**
 * Test class for TaxRateController using Spring Boot Test.
 */
@SpringBootTest
@AutoConfigureMockMvc
public class TaxRateControllerTest {

    /** Mock MVC for testing controller */
    @Autowired
    private MockMvc           mvc;

    /** Reference to tax rate repository */
    @Autowired
    private TaxRateRepository taxRateRepository;

    /**
     * Reset the tax rate to default 2.0 before each test runs.
     */
    @BeforeEach
    public void setUp () {
        // Clean up any existing tax rate
        taxRateRepository.deleteAll();

        // Initialize with default tax rate of 2.0
        TaxRate defaultTaxRate = new TaxRate();
        defaultTaxRate.setId( 1L );
        defaultTaxRate.setRate( 2.0 );
        taxRateRepository.save( defaultTaxRate );
    }

    /**
     * Test getting the current (default) tax rate with authenticated admin
     * user.
     */
    @Test
    @WithMockUser ( username = "user", roles = { "ADMIN" } )
    @Transactional
    public void testGetCurrentTaxRate () {
    	try {
	        String result = mvc.perform( get( "/api/tax-rate" ) ).andExpect( status().isOk() )
	                .andExpect( content().contentType( MediaType.APPLICATION_JSON ) ).andReturn().getResponse()
	                .getContentAsString();
	        assertTrue( result.contains( "\"id\":1,\"rate\":2.0" ) );
    	}
    	catch (Exception e) {
    		fail("Returned unexpected exception: " + e);
    	}
    }

    /**
     * Test getting the current tax rate without user authentication
     */
    @Test
    public void testGetCurrentTaxRateUnauthorized() {
    	try {
    		mvc.perform( get( "/api/tax-rate" ) ).andExpect( status().isUnauthorized() );
    	}
    	catch (Exception e) {
    		fail("Returned unexpected exception: " + e);
    	}
    }

    /**
     * Test updating tax rate with valid rate as admin user
     */
    @Test
    @WithMockUser ( username = "admin", roles = { "ADMIN" } )
    public void testUpdateTaxRateValidRate() {
        // Get the initial tax rate
    	try {
        String result = mvc.perform( get( "/api/tax-rate" ) ).andExpect( status().isOk() )
                .andExpect( content().contentType( MediaType.APPLICATION_JSON ) ).andReturn().getResponse()
                .getContentAsString();
        assertTrue( result.contains( "\"id\":1,\"rate\":2.0" ) );
    	}
    	catch (Exception e) {
    		fail("Returned unexpected exception: " + e);
    	}

        // Update the tax rate from 2
    	try {
	        String updatedResult = mvc
	                .perform( put( "/api/tax-rate" ).contentType( MediaType.APPLICATION_JSON )
	                        .content( "{\"id\":1,\"rate\":5.0}" ) )
	                .andExpect( status().isOk() ).andExpect( content().contentType( MediaType.APPLICATION_JSON ) )
	                .andReturn().getResponse().getContentAsString();
	        assertTrue( updatedResult.contains( "\"id\":1,\"rate\":5.0" ) );
    	}
    	catch (Exception e) {
    		fail("Returned unexpected exception: " + e);
    	}

        // Verify that the tax rate stored in database is updated to 5
        TaxRate savedRate = taxRateRepository.findById( 1L ).orElseThrow();
        assertEquals( 5.0, savedRate.getRate() );
    }

    /**
     * Test updating tax rate with invalid rate of -1 as admin user.
     */
    @Test
    @WithMockUser ( username = "admin", roles = { "ADMIN" } )
    public void testUpdateTaxRateBelowMinimum() {
    	try {
	        mvc.perform(
	                put( "/api/tax-rate" ).contentType( MediaType.APPLICATION_JSON ).content( "{\"id\":1,\"rate\":-1.0}" ) )
	                .andExpect( status().isBadRequest() );
    	}
    	catch (Exception e) {
    		fail("Returned unexpected exception: " + e);
    	}

        // Verify no change in database
        TaxRate savedRate = taxRateRepository.findById( 1L ).orElseThrow();
        assertEquals( 2.0, savedRate.getRate() );
    }

    /**
     * Test updating tax rate with invalid rate of 101 as admin user.
     */
    @Test
    @WithMockUser ( username = "admin", roles = { "ADMIN" } )
    public void testUpdateTaxRateAboveMaximum() {
    	try {
	        mvc.perform( put( "/api/tax-rate" ).contentType( MediaType.APPLICATION_JSON )
	                .content( "{\"id\":1,\"rate\":101.0}" ) ).andExpect( status().isBadRequest() );
    	}
    	catch (Exception e) {
    		fail("Returned unexpected exception: " + e);
    	}

        // Verify no change in database
        TaxRate savedRate = taxRateRepository.findById( 1L ).orElseThrow();
        assertEquals( 2.0, savedRate.getRate() );
    }
    
    /**
     * Test updating tax rate with invalid content as admin user.
     */
    @Test
    @WithMockUser ( username = "admin", roles = { "ADMIN" } )
    public void testUpdateTaxRateInvalidContent() {
    	try {
	        mvc.perform( put( "/api/tax-rate" ).contentType( MediaType.APPLICATION_JSON )
	                .content( "abc" ) ).andExpect( status().isBadRequest() );
    	}
    	catch (Exception e) {
    		fail("Returned unexpected exception: " + e);
    	}

        // Verify no change in database
        TaxRate savedRate = taxRateRepository.findById( 1L ).orElseThrow();
        assertEquals( 2.0, savedRate.getRate() );
    }

    /**
     * Test updating tax rate with rate at minimum boundary (0) as admin user.
     */
    @Test
    @WithMockUser ( username = "admin", roles = { "ADMIN" } )
    public void testUpdateTaxRateAtMinimumBoundary() {
    	try {
	        String result = mvc
	                .perform( put( "/api/tax-rate" ).contentType( MediaType.APPLICATION_JSON )
	                        .content( "{\"id\":1,\"rate\":0.0}" ) )
	                .andExpect( status().isOk() ).andExpect( content().contentType( MediaType.APPLICATION_JSON ) )
	                .andReturn().getResponse().getContentAsString();
	
	        assertTrue( result.contains( "\"id\":1,\"rate\":0.0" ) );
    	}
	    catch (Exception e) {
	    	fail("Returned unexpected exception: " + e);
	    }

        // Verify database change
        TaxRate savedRate = taxRateRepository.findById( 1L ).orElseThrow();
        assertEquals( 0.0, savedRate.getRate() );
    }

    /**
     * Testing to try updating tax rate as a customer user.
     */
    @Test
    @WithMockUser ( username = "user", roles = { "CUSTOMER" } )
    public void testUpdateTaxRateWithoutAdminRole()  {
    	try {
	        mvc.perform(
	                put( "/api/tax-rate" ).contentType( MediaType.APPLICATION_JSON ).content( "{\"id\":1,\"rate\":5.0}" ) )
	                .andExpect( status().isForbidden() );
    	}
    	catch (Exception e) {
    		fail("Returned unexpected exception: " + e);
    	}

        // Verify no change in database
        TaxRate savedRate = taxRateRepository.findById( 1L ).orElseThrow();
        assertEquals( 2.0, savedRate.getRate() );
    }
    
    /**
     * Testing to try getting tax rate as a customer.
     */
    @Test
    @WithMockUser ( username = "user", roles = { "CUSTOMER" } )
    public void testGetTaxRateAsCustomer()  {
    	try {
	        String result = mvc.perform( get( "/api/tax-rate" ) ).andExpect( status().isOk() )
	                .andExpect( content().contentType( MediaType.APPLICATION_JSON ) ).andReturn().getResponse()
	                .getContentAsString();
	        assertTrue( result.contains( "\"id\":1,\"rate\":2.0" ) );
    	}
    	catch (Exception e) {
    		fail("Returned unexpected exception: " + e);
    	}
    }
    
    /**
     * Testing to try getting tax rate as a staff.
     */
    @Test
    @WithMockUser ( username = "user", roles = { "STAFF" } )
    public void testGetTaxRateAsStaff()  {
    	try {
	        String result = mvc.perform( get( "/api/tax-rate" ) ).andExpect( status().isOk() )
	                .andExpect( content().contentType( MediaType.APPLICATION_JSON ) ).andReturn().getResponse()
	                .getContentAsString();
	        assertTrue( result.contains( "\"id\":1,\"rate\":2.0" ) );
    	}
    	catch (Exception e) {
    		fail("Returned unexpected exception: " + e);
    	}
    }
}
