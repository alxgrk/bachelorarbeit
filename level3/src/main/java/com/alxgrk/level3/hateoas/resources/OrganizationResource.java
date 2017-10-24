/*
 * Created on May 18, 2017
 *
 * author age
 */
package com.alxgrk.level3.hateoas.resources;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.core.Relation;
import org.springframework.http.HttpMethod;

import com.alxgrk.level3.controller.OrganizationController;
import com.alxgrk.level3.controller.ResourceController;
import com.alxgrk.level3.hateoas.mapping.OrganizationMapper;
import com.alxgrk.level3.hateoas.mediatype.json.LinkWithMethod;
import com.alxgrk.level3.hateoas.mediatype.json.ResourceSupportWithMethods;
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
public class OrganizationResource extends ResourceSupportWithMethods {

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

        Link selfLink = linkTo(methodOn(OrganizationController.class).getOne(organization.getId()))
                .withSelfRel();
        add(selfLink, HttpMethod.GET);

        return this;
    }

    public OrganizationResource addUpdateLink() {

        Link udpateLink = linkTo(methodOn(ResourceController.class)
                .updateOne(organization.getId(), null))
                        .withRel(Rels.UPDATE);
        add(udpateLink, HttpMethod.PUT);

        return this;
    }

    public OrganizationResource addDeleteLink() {

        Link deleteLink = linkTo(methodOn(OrganizationController.class)
                .deleteOne(organization.getId()))
                        .withRel(Rels.DELETE);
        add(deleteLink, HttpMethod.DELETE);

        return this;
    }

    public OrganizationResource addMembersLink() {

        Link membersLink = linkTo(methodOn(OrganizationController.class)
                .getAllMembers(organization.getId())).withRel(Rels.MEMBERS);
        add(membersLink, HttpMethod.GET);

        return this;
    }

    /**
     * Just a delegate method, that return the resource itself.
     * 
     * @param links
     * @return
     */
    public OrganizationResource addLinks(LinkWithMethod... links) {

        add(links);

        return this;
    }
}
