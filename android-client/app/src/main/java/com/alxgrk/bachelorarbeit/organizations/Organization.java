package com.alxgrk.bachelorarbeit.organizations;

import com.alxgrk.bachelorarbeit.hateoas.Link;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import lombok.Data;

/**
 * The model for a root representation according to
 * {@link com.alxgrk.bachelorarbeit.hateoas.HateoasMediaType#ORGANIZATION_TYPE}.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class Organization {

    @JsonProperty("_links")
    private List<Link> links;

    private Integer id;

    private String name;
}
