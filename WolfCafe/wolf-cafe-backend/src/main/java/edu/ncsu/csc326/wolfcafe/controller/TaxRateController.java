package edu.ncsu.csc326.wolfcafe.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc326.wolfcafe.entity.TaxRate;
import edu.ncsu.csc326.wolfcafe.service.TaxRateService;


/**
 * Controller for tax rate functionality.
 */
@RestController
@RequestMapping ( "/api/tax-rate" )
@CrossOrigin ( "*" )
public class TaxRateController {

    /** Connection to TaxRateService */
    private final TaxRateService taxRateService;

    /** Initializing taxRateService
     * @param taxRateService
     * 				taxRateService to aid taxRateController functionality
     */
    @Autowired
    public TaxRateController ( final TaxRateService taxRateService ) {
        this.taxRateService = taxRateService;
    }

    /**
     * REST API method to provide GET access to tax rate in database
     *
     * @return JSON representation of tax rate
     */
    @GetMapping
    public ResponseEntity<TaxRate> getCurrentTaxRate () {
        return ResponseEntity.ok( taxRateService.getCurrentTaxRate() );
    }

    /**
     * REST API method to provide PUT access to the tax rate
     *
     * @param taxRate
     *            the rate to update with
     * @return ResponseEntity indicating success or failure to update tax rate
     */
    @PutMapping
    @PreAuthorize ( "hasRole('ADMIN')" )
    public ResponseEntity<TaxRate> updateTaxRate ( @RequestBody final TaxRate taxRate ) {
    	if(taxRate == null) {
    		return ResponseEntity.badRequest().build();
    	}
        if ( taxRate.getRate() < 0 || taxRate.getRate() > 100 ) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok( taxRateService.updateTaxRate( taxRate.getRate() ) );
    }
}
