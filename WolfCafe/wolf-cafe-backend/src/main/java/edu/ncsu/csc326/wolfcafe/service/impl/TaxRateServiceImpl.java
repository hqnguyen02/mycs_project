package edu.ncsu.csc326.wolfcafe.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.ncsu.csc326.wolfcafe.entity.TaxRate;
import edu.ncsu.csc326.wolfcafe.repository.TaxRateRepository;
import edu.ncsu.csc326.wolfcafe.service.TaxRateService;
import jakarta.annotation.PostConstruct;


/**
 * Implemented TaxRateService
 */
@Service
@Transactional
public class TaxRateServiceImpl implements TaxRateService {
	/** Stores current tax rate */
    private final TaxRateRepository taxRateRepository;

    /**
     * Constructs TaxRateService
     * 
     * @param taxRateRepository
     * 				repository which contains the current tax rate
     */
    @Autowired
    public TaxRateServiceImpl ( final TaxRateRepository taxRateRepository ) {
        this.taxRateRepository = taxRateRepository;
    }

    @Override
    @PostConstruct
    public void initializeDefaultTaxRate () {
        if ( !taxRateRepository.existsById( 1L ) ) {
            final TaxRate defaultTaxRate = new TaxRate();
            defaultTaxRate.setRate( 2.0 );
            taxRateRepository.save( defaultTaxRate );
        }
    }

    @Override
    public TaxRate getCurrentTaxRate () {
        return taxRateRepository.getCurrentTaxRate().orElseGet( () -> {
            final TaxRate defaultTaxRate = new TaxRate();
            defaultTaxRate.setRate( 2.0 );
            return taxRateRepository.save( defaultTaxRate );
        } );
    }

    @Override
    public TaxRate updateTaxRate ( final Double newRate ) {
        final TaxRate taxRate = getCurrentTaxRate();
        taxRate.setRate( newRate );
        return taxRateRepository.save( taxRate );
    }
}
