package edu.ncsu.csc326.wolfcafe.service;

import edu.ncsu.csc326.wolfcafe.entity.TaxRate;

/**
 * Interface defining the set tax rate behavior
 */
public interface TaxRateService {

    /**
     * Initialize the default tax rate to 2.0
     */
    public void initializeDefaultTaxRate ();

    /**
     * Retrieve the current tax rate
     *
     * @return taxRate
     */
    public TaxRate getCurrentTaxRate ();

    /**
     * Modify the tax rate and save it in DB
     *
     * @param newRate
     * 			new value to set the tax rate to
     * @return taxRate
     */
    public TaxRate updateTaxRate ( final Double newRate );
}
