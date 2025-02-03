/**
 * Test Tax Rate Service
 */
package edu.ncsu.csc326.wolfcafe.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import edu.ncsu.csc326.wolfcafe.entity.TaxRate;
import edu.ncsu.csc326.wolfcafe.repository.TaxRateRepository;

/**
 * Tests Tax Rate Service class
 */
@SpringBootTest
class TaxRateServiceTest {

	/** Reference to tax rate service */
    @Autowired
    private TaxRateService    taxRateService;

    /** Reference to tax rate repository */
    @Autowired
    private TaxRateRepository taxRateRepository;
    

    /**
     * Sets up the test case.
     *
     * @throws java.lang.Exception
     *             if error
     */
    @BeforeEach
    public void setUp () throws Exception {
        taxRateRepository.deleteAll();
    }
    
    @AfterEach
    public void clearDown() throws Exception {
    	taxRateRepository.deleteAll();
    }

    /**
     * Tests that tax rate defaults to 2.0
     */
    @Test
    void testTaxRateDefault () {
        final TaxRate retrievedTaxRate = taxRateService.getCurrentTaxRate();
        assertNotNull( retrievedTaxRate );
        assertEquals( 2.0, retrievedTaxRate.getRate() );
    }

    /**
     * Tests that tax rate can be updated
     */
    @Test
    void testTaxRateUpdate () {

        final TaxRate savedTaxRate = taxRateService.updateTaxRate( 5.0 );

        assertNotNull( savedTaxRate.getId() );
        assertEquals( 5.0, savedTaxRate.getRate() );

        final TaxRate retrievedTaxRate = taxRateService.getCurrentTaxRate();
        assertNotNull( retrievedTaxRate );
        assertEquals( 5.0, retrievedTaxRate.getRate() );
    }

    /**
     * Tests that tax rate is 2.0 even after rate is set to null
     */
    @Test
    void testTaxRateNullUpdate () {

        final TaxRate savedTaxRate = taxRateService.updateTaxRate( null );

        assertEquals( 2.0, savedTaxRate.getRate() );

        final TaxRate retrievedTaxRate = taxRateService.getCurrentTaxRate();
        assertNotNull( retrievedTaxRate );
        assertEquals( 2.0, retrievedTaxRate.getRate() );
    }
}
