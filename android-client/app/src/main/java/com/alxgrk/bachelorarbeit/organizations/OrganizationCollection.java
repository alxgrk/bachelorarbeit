package com.alxgrk.bachelorarbeit.organizations;

import com.alxgrk.bachelorarbeit.hateoas.Link;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collection;
import java.util.List;

import lombok.Data;

@Data
public class OrganizationCollection {

    @JsonProperty("_links")
    List<Link> links;

    @JsonProperty("members")
    Collection<Organization> members;

}
