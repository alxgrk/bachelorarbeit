package com.alxgrk.level2.controller;

import java.net.URI;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alxgrk.level2.error.AlreadyExistsError;
import com.alxgrk.level2.models.Account;
import com.alxgrk.level2.models.wrapper.ShortenedAccount;
import com.alxgrk.level2.repos.AccountRepository;
import com.alxgrk.level2.rest.mapping.AccountMapper;
import com.alxgrk.level2.rest.mapping.ResourceMapper;
import com.alxgrk.level2.rest.rto.AccountRto;
import com.alxgrk.level2.rest.rto.ResourceRto;
import com.alxgrk.level2.util.AccountValidator;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import io.swagger.annotations.Api;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Api(tags = "Accounts", description = "managing accounts")
@RestController
@RequestMapping("/orgs/{orgId}/accounts")
@RequiredArgsConstructor
public class AccountController implements CollectionController<AccountRto> {

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
    @RequestMapping(method = RequestMethod.GET)
    public Collection<AccountRto> getAll() {
        return repository.findAll()
                .stream()
                .map(a -> mapper.accountToAccountRto(a))
                .collect(Collectors.toList());

    }

    // ------------------
    // ABOUT ONE ACCOUNT
    // ------------------

    @Override
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> addOne(@RequestBody AccountRto input) {
        Optional<Account> accountOptional = repository.findByUsername(input.getUsername());

        if (accountOptional.isPresent()) {
            Account account = accountOptional.get();

            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new AlreadyExistsError(account));
        } else {
            Account account = new Account(input.getUsername(), input.getPassword());
            mapper.updateAccountFromAccountRto(input, account);

            repository.save(account);

            return ResponseEntity.created(URI.create("http://localhost:8080/accounts/" + account
                    .getId())).build();
        }
    }

    @Override
    @RequestMapping(
            method = RequestMethod.GET,
            value = "/{accountId}")
    public AccountRto getOne(@PathVariable Long accountId) {
        Account account = validator.validateAccount(accountId);
        return mapper.accountToAccountRto(account);
    }

    @RequestMapping(
            method = RequestMethod.PUT,
            value = "/{accountId}")
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

        return ResponseEntity.created(URI.create("http://localhost:8080/accounts/" + account
                .getId())).build();
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
            value = "/{accountId}/properties")
    public Collection<ResourceRto> getAccountResources(@PathVariable Long accountId) {
        Account account = validator.validateAccount(accountId);
        return account.getConnectedResources()
                .stream()
                .map(r -> ResourceMapper.INSTANCE.resourceToResourceRto(r))
                .collect(Collectors.toList());

    }

}
