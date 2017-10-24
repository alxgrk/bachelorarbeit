package com.alxgrk.bachelorarbeit.resources;

import com.alxgrk.bachelorarbeit.hateoas.Link;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collection;
import java.util.List;

import lombok.Data;

@Data
public class ResourceCollection {

    @JsonProperty("_links")
    List<Link> links;

    @JsonProperty("members")
    Collection<Resource> members;

}
