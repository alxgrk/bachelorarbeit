package com.alxgrk.level3.hateoas.resources;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;

import org.springframework.hateoas.ResourceSupport;

import com.alxgrk.level3.controller.AccountController;
import com.alxgrk.level3.controller.OrganizationController;
import com.alxgrk.level3.controller.ResourceController;
import com.alxgrk.level3.controller.RootController;
import com.alxgrk.level3.hateoas.rels.Rels;

public class RootResource extends ResourceSupport {

    public RootResource() {
        this.add(linkTo(methodOn(RootController.class).getRoot()).withSelfRel());

        this.add(linkTo(methodOn(AccountController.class).getAll()).withRel(Rels.ACCOUNTS));

        this.add(linkTo(methodOn(OrganizationController.class).getAll())
                .withRel(Rels.ORGANIZATIONS));

        this.add(linkTo(methodOn(ResourceController.class).getAll()).withRel(Rels.RESOURCES));
    }

}
