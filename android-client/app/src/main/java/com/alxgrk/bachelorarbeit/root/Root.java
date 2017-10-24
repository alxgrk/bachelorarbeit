package com.alxgrk.bachelorarbeit.root;

import com.alxgrk.bachelorarbeit.hateoas.Link;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import lombok.Data;

/**
 * The model for a root representation according to
 * {@link com.alxgrk.bachelorarbeit.hateoas.HateoasMediaType#ROOT_TYPE}.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class Root {

    @JsonProperty("_links")
    private List<Link> links;

}
