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
import org.springframework.http.HttpMethod;

import com.alxgrk.level3.controller.CollectionController;
import com.alxgrk.level3.hateoas.mediatype.json.LinkWithMethod;
import com.alxgrk.level3.hateoas.mediatype.json.ResourcesWithMethods;
import com.alxgrk.level3.hateoas.rels.Rels;

@SuppressWarnings("unchecked")
public class ResourcesWithLinks<RESOURCE extends ResourceSupport> {

    private final ResourcesWithMethods<RESOURCE> resources;

    private final CollectionController<?, ?> collectionController;

    public ResourcesWithLinks(Collection<RESOURCE> resources,
            CollectionController<?, ?> collectionController) {
        this.resources = new ResourcesWithMethods<>(resources);
        this.collectionController = collectionController;
    }

    public ResourcesWithLinks<RESOURCE> addSelfLink() {
        Link selfLink = linkTo(methodOn(collectionController.getClass()).getAll())
                .withSelfRel();
        resources.add(selfLink, HttpMethod.GET);

        return this;
    }

    public ResourcesWithLinks<RESOURCE> addCreateLink() {
        Link createLink = linkTo(methodOn(collectionController.getClass()).addOne(null))
                .withRel(Rels.CREATE);
        resources.add(createLink, HttpMethod.POST);

        return this;
    }

    public ResourcesWithLinks<RESOURCE> addCustomLinks(LinkWithMethod... links) {
        resources.add(links);

        return this;
    }

    public ResourcesWithMethods<RESOURCE> create() {
        return resources;
    }

}
