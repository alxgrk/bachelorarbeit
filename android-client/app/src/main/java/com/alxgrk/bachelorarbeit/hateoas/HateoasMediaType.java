package com.alxgrk.bachelorarbeit.hateoas;

import org.springframework.http.MediaType;

import lombok.experimental.UtilityClass;

@UtilityClass
public class HateoasMediaType {

    public static final String ROOT = "application/vnd.alxgrk.root.v1+json";

    public static final String ACCOUNT = "application/vnd.alxgrk.account.v1+json";

    public static final String RESOURCE = "application/vnd.alxgrk.resource.v1+json";

    public static final String ORGANIZATION = "application/vnd.alxgrk.organization.v1+json";

    public final static MediaType ROOT_TYPE = new MediaType("application", "vnd.alxgrk.root.v1+json");

    public final static MediaType ACCOUNT_TYPE = new MediaType("application", "vnd.alxgrk.account.v1+json");

    public final static MediaType RESOURCE_TYPE = new MediaType("application", "vnd.alxgrk.resource.v1+json");

    public final static MediaType ORGANIZATION_TYPE = new MediaType("application", "vnd.alxgrk.organization.v1+json");

}
