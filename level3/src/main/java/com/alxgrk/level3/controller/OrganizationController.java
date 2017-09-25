package com.alxgrk.level3.controller;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alxgrk.level3.error.AlreadyExistsError;
import com.alxgrk.level3.hateoas.mapping.OrganizationMapper;
import com.alxgrk.level3.hateoas.rels.Rels;
import com.alxgrk.level3.hateoas.resources.AccountResource;
import com.alxgrk.level3.hateoas.resources.OrganizationResource;
import com.alxgrk.level3.hateoas.resources.ResourcesWithLinks;
import com.alxgrk.level3.hateoas.rto.OrganizationRto;
import com.alxgrk.level3.models.Account;
import com.alxgrk.level3.models.Organization;
import com.alxgrk.level3.repos.AccountRepository;
import com.alxgrk.level3.repos.OrganizationRepository;
import com.alxgrk.level3.util.OrganizationValidator;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;

@Api(tags = "Organizations", description = "managing organizations")
@RestController
@RequestMapping("/orgs")
@RequiredArgsConstructor
public class OrganizationController implements
        CollectionController<OrganizationRto, OrganizationResource> {

    @Autowired
    private final OrganizationRepository organizationRepository;

    @Autowired
    private final AccountRepository accountRepository;

    @Qualifier("organization")
    @Autowired
    private final OrganizationValidator validator;

    @Autowired
    private final OrganizationMapper mapper;

    // -----------------------
    // ABOUT ALL ORGANIZATIONS
    // -----------------------

    @Override
    @RequestMapping(method = RequestMethod.GET)
    public Resources<OrganizationResource> getAll() {
        List<OrganizationResource> organizationResources = organizationRepository.findAll()
                .stream()
                .map(OrganizationResource::new)
                .map((r) -> r.addSelfLink()
                        .addMembersLink())
                .collect(Collectors.toList());

        return new ResourcesWithLinks<>(organizationResources, this)
                .addSelfLink().create();
    }

    // ----------------------
    // ABOUT ONE ORGANIZATION
    // ----------------------

    @Override
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> addOne(@RequestBody OrganizationRto input) {
        Optional<Organization> organizationOptional = organizationRepository.findByName(input
                .getName());

        if (organizationOptional.isPresent()) {
            Organization org = organizationOptional.get();
            Link selfLink = new OrganizationResource(org)
                    .addSelfLink()
                    .getLink(Link.REL_SELF);

            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new AlreadyExistsError(org, selfLink));
        } else {
            Organization newOrganization = mapper.organizationRtoToOrganization(input);

            organizationRepository.save(newOrganization);

            Link forOneAccount = new OrganizationResource(newOrganization)
                    .addSelfLink()
                    .getLink(Link.REL_SELF);

            return ResponseEntity.created(URI.create(forOneAccount.getHref())).build();
        }
    }

    @Override
    @RequestMapping(method = RequestMethod.GET, value = "/{orgId}")
    public OrganizationResource getOne(@PathVariable Long orgId) {
        Organization organization = validator.validateOrganization(orgId);

        OrganizationResource organizationResource = new OrganizationResource(organization)
                .addSelfLink()
                .addMembersLink();

        return organizationResource;
    }

    @Override
    @RequestMapping(method = RequestMethod.PUT, value = "/{orgId}")
    public ResponseEntity<?> updateOne(@PathVariable Long orgId,
            @RequestBody OrganizationRto input) {
        Organization organization = validator.validateOrganization(orgId);

        mapper.updateOrganizationFromOrganizationRto(input, organization);

        organizationRepository.save(organization);

        Link forOneOrganization = new OrganizationResource(organization)
                .addSelfLink()
                .getLink(Link.REL_SELF);

        return ResponseEntity.created(URI.create(forOneOrganization.getHref())).build();
    }

    @Override
    @RequestMapping(
            method = RequestMethod.DELETE,
            value = "/{orgId}")
    public ResponseEntity<?> deleteOne(@PathVariable Long orgId) {
        organizationRepository.delete(orgId);

        return ResponseEntity.noContent().build();
    }

    // ----------------------------
    // ABOUT ORGANIZATION'S MEMBERS
    // ----------------------------

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/{orgId}/accounts")
    public Resources<AccountResource> getAllMembers(@PathVariable Long orgId) {
        Organization organization = validator.validateOrganization(orgId);

        // prepare each account
        List<AccountResource> accountResources = organization.getMembers()
                .stream()
                .map(AccountResource::new)
                .map((r) -> {
                    Link detachLink = linkTo(methodOn(OrganizationController.class)
                            .detachMemberFromOrganization(orgId, r.getAccount().getId()))
                                    .withRel(Rels.DETACH);

                    return r.addSelfLink()
                            .addLinks(detachLink);
                })
                .collect(Collectors.toList());

        // prepare the surrounding resource

        Link attachLink = linkTo(methodOn(OrganizationController.class)
                .attachMemberToOrganization(orgId, null))
                        .withRel(Rels.ATTACH);

        return new ResourcesWithLinks<>(accountResources, this)
                .addSelfLink()
                .addCustomLinks(attachLink)
                .create();
    }

    @RequestMapping(
            method = RequestMethod.POST,
            value = "/{orgId}/accounts")
    @Transactional
    public ResponseEntity<?> attachMemberToOrganization(@PathVariable Long orgId,
            @RequestParam("username") String username) {
        Organization organization = validator.validateOrganization(orgId);

        Optional<Account> accountOptional = accountRepository.findByUsername(username);

        if (!accountOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        } else {
            Account account = accountOptional.get();
            account.setOrganization(organization);

            Set<Account> members = organization.getMembers();
            members.add(account);
            organization.setMembers(members);

            Link selfLink = new OrganizationResource(organization)
                    .addMembersLink()
                    .getLink(Rels.MEMBERS);
            return ResponseEntity.created(URI.create(selfLink.getHref())).build();
        }
    }

    @RequestMapping(
            method = RequestMethod.DELETE,
            value = "/{orgId}/accounts/{accountId}")
    @Transactional
    public ResponseEntity<?> detachMemberFromOrganization(@PathVariable Long orgId,
            @PathVariable Long accountId) {
        Organization organization = validator.validateOrganization(orgId);

        Account account = validator.validateAccount(accountId);
        account.setOrganization(null);

        Set<Account> members = organization.getMembers();
        members.remove(account);
        organization.setMembers(members);

        Link selfLink = new OrganizationResource(organization)
                .addMembersLink()
                .getLink(Rels.MEMBERS);
        return ResponseEntity.noContent()
                .location(URI.create(selfLink.getHref()))
                .build();
    }
}
