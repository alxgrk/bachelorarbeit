/*
 * Created on May 22, 2017
 *
 * author age
 */
package com.alxgrk.level3.hateoas.resources;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.core.Relation;
import org.springframework.http.HttpMethod;

import com.alxgrk.level3.controller.ResourceController;
import com.alxgrk.level3.hateoas.mapping.ResourceMapper;
import com.alxgrk.level3.hateoas.mediatype.json.LinkWithMethod;
import com.alxgrk.level3.hateoas.mediatype.json.ResourceSupportWithMethods;
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
public class ResourceResource extends ResourceSupportWithMethods {

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

        Link selfLink = linkTo(methodOn(ResourceController.class).getOne(resource.getId()))
                .withSelfRel();
        add(selfLink, HttpMethod.GET);

        return this;
    }

    public ResourceResource addUpdateLink() {

        Link udpateLink = linkTo(methodOn(ResourceController.class)
                .updateOne(resource.getId(), null))
                        .withRel(Rels.UPDATE);
        add(udpateLink, HttpMethod.PUT);

        return this;
    }

    public ResourceResource addDeleteLink() {

        Link deleteLink = linkTo(methodOn(ResourceController.class)
                .deleteOne(resource.getId()))
                        .withRel(Rels.DELETE);
        add(deleteLink, HttpMethod.DELETE);

        return this;
    }

    public ResourceResource addAdministratorsLink() {

        Link adminsLink = linkTo(methodOn(ResourceController.class)
                .getAllAdministrators(resource.getId())).withRel(Rels.ADMINISTRATORS);
        add(adminsLink, HttpMethod.GET);

        return this;
    }

    /**
     * Just a delegate method, that return the resource itself.
     * 
     * @param links
     * @return
     */
    public ResourceResource addLinks(LinkWithMethod... links) {

        add(links);

        return this;
    }
}
