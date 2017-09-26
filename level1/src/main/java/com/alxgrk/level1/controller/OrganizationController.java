package com.alxgrk.level1.controller;

import java.net.URI;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alxgrk.level1.error.AlreadyExistsError;
import com.alxgrk.level1.models.Account;
import com.alxgrk.level1.models.Organization;
import com.alxgrk.level1.repos.AccountRepository;
import com.alxgrk.level1.repos.OrganizationRepository;
import com.alxgrk.level1.rest.mapping.AccountMapper;
import com.alxgrk.level1.rest.mapping.OrganizationMapper;
import com.alxgrk.level1.rest.rto.AccountRto;
import com.alxgrk.level1.rest.rto.OrganizationRto;
import com.alxgrk.level1.util.OrganizationValidator;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;

@Api(tags = "Organizations", description = "managing organizations")
@RestController
@RequestMapping("/orgs")
@RequiredArgsConstructor
public class OrganizationController implements CollectionController<OrganizationRto> {

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
    @RequestMapping(path = "/get-all", method = RequestMethod.POST)
    public Collection<OrganizationRto> getAll() {
        return organizationRepository.findAll()
                .stream()
                .map(o -> mapper.organizationToOrganizationRto(o))
                .collect(Collectors.toList());

    }

    // ----------------------
    // ABOUT ONE ORGANIZATION
    // ----------------------

    @Override
    @RequestMapping(path = "/create", method = RequestMethod.POST)
    public ResponseEntity<?> addOne(@RequestBody OrganizationRto input) {
        Optional<Organization> organizationOptional = organizationRepository.findByName(input
                .getName());

        if (organizationOptional.isPresent()) {
            Organization org = organizationOptional.get();

            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new AlreadyExistsError(org));
        } else {
            Organization newOrganization = mapper.organizationRtoToOrganization(input);

            organizationRepository.save(newOrganization);

            return ResponseEntity.created(URI.create("http://localhost:8080/orgs/" + newOrganization
                    .getId())).build();
        }
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "/get-one")
    public OrganizationRto getOne(@RequestParam Long orgId) {
        Organization organization = validator.validateOrganization(orgId);

        return mapper.organizationToOrganizationRto(organization);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "/update")
    public ResponseEntity<?> updateOne(@RequestParam Long orgId,
            @RequestBody OrganizationRto input) {
        Organization organization = validator.validateOrganization(orgId);

        mapper.updateOrganizationFromOrganizationRto(input, organization);

        organizationRepository.save(organization);

        return ResponseEntity.created(URI.create("http://localhost:8080/orgs/" + organization
                .getId())).build();
    }

    @Override
    @RequestMapping(
            method = RequestMethod.POST,
            value = "/delete")
    public ResponseEntity<?> deleteOne(@RequestParam Long orgId) {
        organizationRepository.delete(orgId);

        return ResponseEntity.noContent().build();
    }

    // ----------------------------
    // ABOUT ORGANIZATION'S MEMBERS
    // ----------------------------

    @RequestMapping(
            method = RequestMethod.POST,
            value = "/get-members-of-org")
    public Collection<AccountRto> getAllMembers(@RequestParam Long orgId) {
        Organization organization = validator.validateOrganization(orgId);

        return organization.getMembers()
                .stream()
                .map(a -> AccountMapper.INSTANCE.accountToAccountRto(a))
                .collect(Collectors.toList());
    }

    @RequestMapping(
            method = RequestMethod.POST,
            value = "/add-member-to-org")
    @Transactional
    public ResponseEntity<?> attachMemberToOrganization(@RequestParam Long orgId,
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

            return ResponseEntity.created(URI.create("http://localhost:8080/orgs/" + organization
                    .getId() + "/accounts")).build();
        }
    }

    @RequestMapping(
            method = RequestMethod.POST,
            value = "delete-member-from-org")
    @Transactional
    public ResponseEntity<?> detachMemberFromOrganization(@RequestParam Long orgId,
            @RequestParam Long accountId) {
        Organization organization = validator.validateOrganization(orgId);

        Account account = validator.validateAccount(accountId);
        account.setOrganization(null);

        Set<Account> members = organization.getMembers();
        members.remove(account);
        organization.setMembers(members);

        return ResponseEntity.noContent()
                .location(URI.create("http://localhost:8080/orgs/" + organization
                        .getId() + "/accounts"))
                .build();
    }
}
