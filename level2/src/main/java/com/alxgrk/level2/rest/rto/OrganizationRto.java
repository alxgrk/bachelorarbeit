/*
 * Created on May 16, 2017
 *
 * author age
 */
package com.alxgrk.level2.rest.rto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class OrganizationRto {

    @JsonProperty(access = Access.READ_ONLY)
    private Long id;

    private String name;

}
