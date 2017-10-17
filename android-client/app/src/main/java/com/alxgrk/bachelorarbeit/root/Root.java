package com.alxgrk.bachelorarbeit.root;

import com.alxgrk.bachelorarbeit.hateoas.Link;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import lombok.Data;

/**
 * The model for a root representation according to
 * {@link com.alxgrk.bachelorarbeit.hateoas.HateoasMediaType#ROOT_TYPE}.
 */
@Data
public class Root {

    @JsonProperty("_links")
    List<Link> links;

}
