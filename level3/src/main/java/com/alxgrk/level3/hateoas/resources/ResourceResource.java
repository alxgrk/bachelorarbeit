/*
 * Created on May 22, 2017
 *
 * author age
 */
package com.alxgrk.level3.hateoas.resources;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.core.Relation;

import com.alxgrk.level3.controller.ResourceController;
import com.alxgrk.level3.hateoas.mapping.ResourceMapper;
import com.alxgrk.level3.hateoas.rels.Rels;
import com.alxgrk.level3.hateoas.rto.ResourceRto;
import com.alxgrk.level3.models.Resource;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import lombok.Getter;

@Getter
@Relation(
        value = ResourceResource.RESOURCE_NAME,
        collectionRelation = ResourceResource.RESOURCE_NAME + 's')
public class ResourceResource extends ResourceSupport {

    public static final String RESOURCE_NAME = "resource";

    @JsonIgnore
    private final Resource resource;

    @JsonUnwrapped
    private final ResourceRto resourceRto;

    public ResourceResource(Resource resource) {
        this.resource = resource;

        resourceRto = ResourceMapper.INSTANCE.resourceToResourceRto(resource);
    }

    public ResourceResource addSelfLink() {

        add(linkTo(methodOn(ResourceController.class).getOne(resource.getId()))
                .withSelfRel());

        return this;
    }

    public ResourceResource addAdministratorsLink() {

        add(linkTo(methodOn(ResourceController.class).getAllAdministrators(resource.getId()))
                .withRel(Rels.ADMINISTRATORS));

        return this;
    }

    /**
     * Just a delegate method, that return the resource itself.
     * 
     * @param links
     * @return
     */
    public ResourceResource addLinks(Link... links) {

        add(links);

        return this;
    }
}
