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

import com.alxgrk.level3.controller.AccountController;
import com.alxgrk.level3.controller.OrganizationController;
import com.alxgrk.level3.hateoas.mapping.AccountMapper;
import com.alxgrk.level3.hateoas.rels.Rels;
import com.alxgrk.level3.hateoas.rto.AccountRto;
import com.alxgrk.level3.models.Account;
import com.alxgrk.level3.models.Organization;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import lombok.Getter;

@Getter
@Relation(
        value = AccountResource.RESOURCE_NAME,
        collectionRelation = AccountResource.RESOURCE_NAME + 's')
public class AccountResource extends ResourceSupport {

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

        add(linkTo(methodOn(AccountController.class).getOne(account.getId()))
                .withSelfRel());

        return this;
    }

    public AccountResource addAccountOrgLink() {

        Organization organization = account.getOrganization();
        if (null != organization) {
            add(linkTo(methodOn(OrganizationController.class)
                    .getOne(organization.getId()))
                            .withRel(Rels.ORGANIZATION));
        }

        return this;
    }

    public AccountResource addAccountResourcesLink() {

        add(linkTo(methodOn(AccountController.class).getAccountResources(account.getId()))
                .withRel(Rels.RESOURCES));

        return this;
    }

    /**
     * Just a delegate method, that return the resource itself.
     * 
     * @param links
     * @return
     */
    public AccountResource addLinks(Link... links) {

        add(links);

        return this;
    }

}
