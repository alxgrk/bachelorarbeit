/*
 * Created on May 18, 2017
 *
 * author age
 */
package com.alxgrk.level3.hateoas.resources;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.core.Relation;

import com.alxgrk.level3.controller.OrganizationController;
import com.alxgrk.level3.hateoas.mapping.OrganizationMapper;
import com.alxgrk.level3.hateoas.rels.Rels;
import com.alxgrk.level3.hateoas.rto.OrganizationRto;
import com.alxgrk.level3.models.Organization;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import lombok.Getter;

@Getter
@Relation(
        value = OrganizationResource.RESOURCE_NAME,
        collectionRelation = OrganizationResource.RESOURCE_NAME + 's')
public class OrganizationResource extends ResourceSupport {

    public static final String RESOURCE_NAME = "organization";

    @JsonIgnore
    private final Organization organization;

    @JsonUnwrapped
    private final OrganizationRto organizationRto;

    public OrganizationResource(Organization organization) {
        this.organization = organization;

        organizationRto = OrganizationMapper.INSTANCE.organizationToOrganizationRto(organization);
    }

    public OrganizationResource addSelfLink() {

        add(linkTo(methodOn(OrganizationController.class).getOne(organization.getId()))
                .withSelfRel());

        return this;
    }

    public OrganizationResource addMembersLink() {

        add(linkTo(methodOn(OrganizationController.class).getAllMembers(organization.getId()))
                .withRel(Rels.MEMBERS));

        return this;
    }

    /**
     * Just a delegate method, that return the resource itself.
     * 
     * @param links
     * @return
     */
    public OrganizationResource addLinks(Link... links) {

        add(links);

        return this;
    }
}
