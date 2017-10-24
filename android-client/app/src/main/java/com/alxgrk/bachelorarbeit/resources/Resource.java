package com.alxgrk.bachelorarbeit.resources;

import com.alxgrk.bachelorarbeit.hateoas.Link;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import lombok.Data;

/**
 * The model for a root representation according to
 * {@link com.alxgrk.bachelorarbeit.hateoas.HateoasMediaType#RESOURCE_TYPE}.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class Resource {

    @JsonProperty("_links")
    private List<Link> links;

    private Integer id;

    private String name;

    private List<Timeslot> availableTimeslots;

    private List<Timeslot> bookedTimeslots;
}
