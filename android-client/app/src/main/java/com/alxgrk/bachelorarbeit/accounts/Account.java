package com.alxgrk.bachelorarbeit.accounts;

import com.alxgrk.bachelorarbeit.hateoas.Link;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import lombok.Data;

/**
 * The model for a root representation according to
 * {@link com.alxgrk.bachelorarbeit.hateoas.HateoasMediaType#ACCOUNT_TYPE}.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class Account {

    @JsonProperty("_links")
    private List<Link> links;

    private Integer id;

    private String username;

    private String surname;

    private String name;
}
