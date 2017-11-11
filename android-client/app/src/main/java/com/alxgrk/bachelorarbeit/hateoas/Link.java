package com.alxgrk.bachelorarbeit.hateoas;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Link {

    private String rel;

    private String href;

    private String method;

}
