package edu.ncsu.csc326.wolfcafe.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import edu.ncsu.csc326.wolfcafe.entity.TaxRate;

/**
 * Repository interface for Tax Rate.
 */
@Repository
public interface TaxRateRepository extends JpaRepository<TaxRate, Long> {
    /**
     * Retrieves current tax rate
     *
     * @return taxRate
     */

    @Query ( "SELECT t FROM TaxRate t WHERE t.id = 1L" )
    Optional<TaxRate> getCurrentTaxRate ();
}
