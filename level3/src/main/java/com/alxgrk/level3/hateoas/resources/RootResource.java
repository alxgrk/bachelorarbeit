package com.alxgrk.level3.hateoas.resources;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;

import org.springframework.hateoas.Link;
import org.springframework.http.HttpMethod;

import com.alxgrk.level3.controller.AccountController;
import com.alxgrk.level3.controller.OrganizationController;
import com.alxgrk.level3.controller.ResourceController;
import com.alxgrk.level3.controller.RootController;
import com.alxgrk.level3.hateoas.mediatype.json.ResourceSupportWithMethods;
import com.alxgrk.level3.hateoas.rels.Rels;

public class RootResource extends ResourceSupportWithMethods {

    public RootResource() {
        Link selfLink = linkTo(methodOn(RootController.class).getRoot())
                .withSelfRel();
        this.add(selfLink, HttpMethod.GET);

        Link accountsLink = linkTo(methodOn(AccountController.class).getAll())
                .withRel(Rels.ACCOUNTS);
        this.add(accountsLink, HttpMethod.GET);

        Link orgsLink = linkTo(methodOn(OrganizationController.class).getAll())
                .withRel(Rels.ORGANIZATIONS);
        this.add(orgsLink, HttpMethod.GET);

        Link resourcesLink = linkTo(methodOn(ResourceController.class).getAll())
                .withRel(Rels.RESOURCES);
        this.add(resourcesLink, HttpMethod.GET);
    }

}
