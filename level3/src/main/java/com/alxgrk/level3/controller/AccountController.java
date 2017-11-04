package com.alxgrk.level3.controller;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;
import static org.springframework.http.HttpMethod.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alxgrk.level3.error.AlreadyExistsError;
import com.alxgrk.level3.hateoas.mapping.AccountMapper;
import com.alxgrk.level3.hateoas.mediatype.MediaTypes;
import com.alxgrk.level3.hateoas.mediatype.json.LinkWithMethod;
import com.alxgrk.level3.hateoas.mediatype.json.ResourcesWithMethods;
import com.alxgrk.level3.hateoas.rels.Rels;
import com.alxgrk.level3.hateoas.resources.AccountResource;
import com.alxgrk.level3.hateoas.resources.ResourceResource;
import com.alxgrk.level3.hateoas.resources.ResourcesWithLinks;
import com.alxgrk.level3.hateoas.rto.AccountRto;
import com.alxgrk.level3.models.Account;
import com.alxgrk.level3.models.wrapper.ShortenedAccount;
import com.alxgrk.level3.repos.AccountRepository;
import com.alxgrk.level3.util.AccountValidator;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import io.swagger.annotations.Api;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Api(tags = "Accounts", description = "managing accounts")
@RestController
@RequestMapping("/orgs/1/accounts") // quick fix to make links work
@RequiredArgsConstructor
public class AccountController implements CollectionController<AccountRto, AccountResource> {

    @Autowired
    private final AccountRepository repository;

    @Autowired
    private final AccountValidator validator;

    @Autowired
    private final AccountMapper mapper;

    // ------------------
    // ABOUT ALL ACCOUNTS
    // ------------------

    @Override
    @RequestMapping(method = RequestMethod.GET, produces = MediaTypes.ACCOUNT_TYPE)
    public ResourcesWithMethods<AccountResource> getAll() {
        List<AccountResource> accountResources = repository.findAll()
                .stream()
                .map(AccountResource::new)
                .map((r) -> r.addSelfLink()
                        .addAccountOrgLink()
                        .addAccountResourcesLink())
                .collect(Collectors.toList());

        return new ResourcesWithLinks<>(accountResources, this)
                .addSelfLink()
                .addCreateLink()
                .create();
    }

    // ------------------
    // ABOUT ONE ACCOUNT
    // ------------------

    @Override
    @RequestMapping(method = RequestMethod.POST, consumes = MediaTypes.ACCOUNT_TYPE)
    public ResponseEntity<?> addOne(@RequestBody AccountRto input) {
        Optional<Account> accountOptional = repository.findByUsername(input.getUsername());

        if (accountOptional.isPresent()) {
            Account account = accountOptional.get();
            Link selfLink = new AccountResource(account)
                    .addSelfLink()
                    .getLink(Link.REL_SELF);

            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new AlreadyExistsError(account, selfLink));
        } else {
            Account account = new Account(input.getUsername(), input.getPassword());
            mapper.updateAccountFromAccountRto(input, account);

            repository.save(account);

            Link forOneAccount = new AccountResource(account)
                    .addSelfLink()
                    .getLink(Link.REL_SELF);

            return ResponseEntity.created(URI.create(forOneAccount.getHref())).build();
        }
    }

    @Override
    @RequestMapping(
            method = RequestMethod.GET,
            value = "/{accountId}",
            produces = MediaTypes.ACCOUNT_TYPE)
    public AccountResource getOne(@PathVariable Long accountId) {
        Account account = validator.validateAccount(accountId);
        AccountResource accountResource = new AccountResource(account);

        accountResource.addSelfLink()
                .addUpdateLink()
                .addDeleteLink()
                .addAccountOrgLink()
                .addAccountResourcesLink();

        return accountResource;
    }

    @RequestMapping(
            method = RequestMethod.PUT,
            value = "/{accountId}",
            consumes = MediaTypes.ACCOUNT_TYPE)
    public ResponseEntity<?> updateOne(@PathVariable Long accountId,
            @RequestBody @JsonUnwrapped ShortenedAccount input) {
        // TODO correct unwrapping of ShortenedAccount
        AccountRto translatedAccount = new AccountRto()
                .setUsername(input.getUsername())
                .setSurname(input.getSurname())
                .setName(input.getName());

        return updateOne(accountId, translatedAccount);
    }

    @Override
    public ResponseEntity<?> updateOne(@NonNull Long accountId, @NonNull AccountRto input) {
        Account account = validator.validateAccount(accountId);

        input.setPassword(account.getPassword());
        mapper.updateAccountFromAccountRto(input, account);

        repository.save(account);

        Link forOneAccount = new AccountResource(account)
                .addSelfLink()
                .getLink(Link.REL_SELF);

        return ResponseEntity.created(URI.create(forOneAccount.getHref())).build();
    }

    @Override
    @RequestMapping(
            method = RequestMethod.DELETE,
            value = "/{accountId}")
    public ResponseEntity<?> deleteOne(@PathVariable Long accountId) {
        repository.delete(accountId);

        return ResponseEntity.noContent().build();
    }

    // ------------------
    // ABOUT ONE ACCOUNT'S RESOURCES
    // ------------------

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/{accountId}/properties",
            produces = MediaTypes.RESOURCE_TYPE)
    public ResourcesWithMethods<ResourceResource> getAccountResources(
            @PathVariable Long accountId) {
        Account account = validator.validateAccount(accountId);

        // prepare each resource
        List<ResourceResource> accountResources = account.getConnectedResources()
                .stream()
                .map(ResourceResource::new)
                .map((r) -> {
                    Link detachLink = linkTo(methodOn(ResourceController.class)
                            .detachMemberFromResource(r.getResource().getId(), accountId))
                                    .withRel(Rels.DETACH);

                    return r.addSelfLink()
                            .addLinks(new LinkWithMethod(detachLink, DELETE));
                })
                .collect(Collectors.toList());

        // prepare surrounding account
        Link selfLink = linkTo(methodOn(AccountController.class)
                .getAccountResources(accountId))
                        .withSelfRel();

        return new ResourcesWithLinks<>(accountResources, this)
                .addCustomLinks(new LinkWithMethod(selfLink, GET))
                .create();
    }

    // ------------------
    // ABOUT ONE ACCOUNT'S ORGANIZATION
    // ------------------

    // --- NOT NECESSARY, EXPRESSED THROUGH LINKS ---
}
