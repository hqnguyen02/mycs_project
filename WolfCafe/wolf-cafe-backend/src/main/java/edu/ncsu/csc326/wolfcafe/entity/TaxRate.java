package edu.ncsu.csc326.wolfcafe.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Tax rate entity.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table ( name = "tax_rate" )
public class TaxRate {
    /** id for tax rate */
    @Id
    private Long   id = 1L;

    /** taxRate as a double */
    private Double rate;

    /** Set default taxRate to 2.0 at program start */
    @PrePersist
    @PreUpdate
    public void prePersist () {
        if ( rate == null ) {
            rate = 2.0;
        }
        id = 1L;
    }
}
