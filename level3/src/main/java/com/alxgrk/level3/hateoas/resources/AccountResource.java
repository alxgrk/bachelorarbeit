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

import com.alxgrk.level3.controller.AccountController;
import com.alxgrk.level3.controller.OrganizationController;
import com.alxgrk.level3.hateoas.mapping.AccountMapper;
import com.alxgrk.level3.hateoas.mediatype.json.LinkWithMethod;
import com.alxgrk.level3.hateoas.mediatype.json.ResourceSupportWithMethods;
import com.alxgrk.level3.hateoas.rels.Rels;
import com.alxgrk.level3.hateoas.rto.AccountRto;
import com.alxgrk.level3.models.Account;
import com.alxgrk.level3.models.Organization;
import com.alxgrk.level3.models.wrapper.ShortenedAccount;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import lombok.Getter;

@Getter
@Relation(
        value = AccountResource.RESOURCE_NAME,
        collectionRelation = AccountResource.RESOURCE_NAME + 's')
public class AccountResource extends ResourceSupportWithMethods {

    public static final String RESOURCE_NAME = "account";

    @JsonIgnore
    private final Account account;

    @JsonUnwrapped
    private final AccountRto accountRto;

    public AccountResource(Account account) {
        this.account = account;

        accountRto = AccountMapper.INSTANCE.accountToAccountRto(account);
    }

    public AccountResource addSelfLink() {

        Link selfLink = linkTo(methodOn(AccountController.class).getOne(account.getId()))
                .withSelfRel();
        add(selfLink, HttpMethod.GET);

        return this;
    }

    public AccountResource addUpdateLink() {

        Link udpateLink = linkTo(methodOn(AccountController.class)
                .updateOne(account.getId(), (ShortenedAccount) null))
                        .withRel(Rels.UPDATE);
        add(udpateLink, HttpMethod.PUT);

        return this;
    }

    public AccountResource addDeleteLink() {

        Link deleteLink = linkTo(methodOn(AccountController.class)
                .deleteOne(account.getId()))
                        .withRel(Rels.DELETE);
        add(deleteLink, HttpMethod.DELETE);

        return this;
    }

    public AccountResource addAccountOrgLink() {

        Organization organization = account.getOrganization();
        if (null != organization) {
            Link orgLink = linkTo(methodOn(OrganizationController.class)
                    .getOne(organization.getId())).withRel(Rels.ORGANIZATION);
            add(orgLink, HttpMethod.GET);
        }

        return this;
    }

    public AccountResource addAccountResourcesLink() {

        Link resourcesLink = linkTo(methodOn(AccountController.class).getAccountResources(account
                .getId()))
                        .withRel(Rels.RESOURCES);
        add(resourcesLink, HttpMethod.GET);

        return this;
    }

    /**
     * Just a delegate method, that return the resource itself.
     * 
     * @param links
     * @return
     */
    public AccountResource addLinks(LinkWithMethod... links) {

        add(links);

        return this;
    }

}
