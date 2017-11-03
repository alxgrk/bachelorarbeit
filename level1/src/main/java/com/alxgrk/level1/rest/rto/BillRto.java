/*
 * Created on Nov 3, 2017
 *
 * author age
 */
package com.alxgrk.level1.rest.rto;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Data
public class BillRto {

    @JsonProperty(access = Access.READ_ONLY)
    private Long id;

    private BigDecimal amount;

    private String currency;

}
