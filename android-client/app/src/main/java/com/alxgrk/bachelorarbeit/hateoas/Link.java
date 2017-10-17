package com.alxgrk.bachelorarbeit.hateoas;

import lombok.Data;

@Data
public class Link {

    private String rel;

    private String href;

    private String method;

}
