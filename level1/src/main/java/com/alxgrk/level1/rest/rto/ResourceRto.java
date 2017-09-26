package com.alxgrk.level1.rest.rto;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.ElementCollection;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Data
public class ResourceRto {

    @JsonProperty(access = Access.READ_ONLY)
    private Long id;

    private String name;

    @ElementCollection
    private List<TimeslotRto> availableTimeslots = new ArrayList<>();

    @ElementCollection
    private List<TimeslotRto> bookedTimeslots = new ArrayList<>();

}
