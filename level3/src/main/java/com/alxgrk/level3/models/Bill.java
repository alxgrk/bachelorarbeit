/*
 * Created on Nov 3, 2017
 *
 * author age
 */
package com.alxgrk.level3.models;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.Accessors;

@Entity
@Accessors(chain = true)
@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Bill {

    @Id
    @GeneratedValue
    private Long id;

    @NonNull
    @Column(nullable = false)
    private BigDecimal amount;

    @NonNull
    @Column(nullable = false)
    private String currency;

}
