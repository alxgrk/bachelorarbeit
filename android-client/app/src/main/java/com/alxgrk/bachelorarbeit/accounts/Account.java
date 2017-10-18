package com.alxgrk.bachelorarbeit.accounts;

import com.alxgrk.bachelorarbeit.hateoas.Link;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import lombok.Data;

/**
 * The model for a root representation according to
 * {@link com.alxgrk.bachelorarbeit.hateoas.HateoasMediaType#ROOT_TYPE}.
 */
@Data
public class Account {

    @JsonProperty("_links")
    List<Link> links;

    Integer id;

    String username;

    String surname;

    String name;
}
