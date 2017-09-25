/*
 * Created on May 24, 2017
 *
 * author age
 */
package com.alxgrk.level3.hateoas.resources;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;

import java.util.Collection;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.Resources;

import com.alxgrk.level3.controller.CollectionController;

public class ResourcesWithLinks<RESOURCE extends ResourceSupport> {

    private final Resources<RESOURCE> resources;

    private final CollectionController<?, ?> collectionController;

    public ResourcesWithLinks(Collection<RESOURCE> resources,
            CollectionController<?, ?> collectionController) {
        this.resources = new Resources<>(resources);
        this.collectionController = collectionController;
    }

    public ResourcesWithLinks<RESOURCE> addSelfLink() {
        resources.add(linkTo(methodOn(collectionController.getClass()).getAll())
                .withSelfRel());

        return this;
    }

    public ResourcesWithLinks<RESOURCE> addCustomLinks(Link... links) {
        resources.add(links);

        return this;
    }

    public Resources<RESOURCE> create() {
        return resources;
    }

}
