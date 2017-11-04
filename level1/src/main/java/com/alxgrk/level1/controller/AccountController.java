package com.alxgrk.level1.controller;

import java.net.URI;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alxgrk.level1.error.AlreadyExistsError;
import com.alxgrk.level1.models.Account;
import com.alxgrk.level1.models.wrapper.ShortenedAccount;
import com.alxgrk.level1.repos.AccountRepository;
import com.alxgrk.level1.rest.mapping.AccountMapper;
import com.alxgrk.level1.rest.mapping.ResourceMapper;
import com.alxgrk.level1.rest.rto.AccountRto;
import com.alxgrk.level1.rest.rto.ResourceRto;
import com.alxgrk.level1.util.AccountValidator;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import io.swagger.annotations.Api;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Api(tags = "Accounts", description = "managing accounts")
@RestController
@RequestMapping("/accounts")
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
    @RequestMapping(path = "/get-all", method = RequestMethod.POST)
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
    @RequestMapping(path = "/create", method = RequestMethod.POST)
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
            method = RequestMethod.POST,
            value = "/get-one")
    public AccountRto getOne(@RequestParam Long accountId) {
        Account account = validator.validateAccount(accountId);
        return mapper.accountToAccountRto(account);
    }

    @RequestMapping(
            method = RequestMethod.POST,
            value = "/update")
    public ResponseEntity<?> updateOne(@RequestParam Long accountId,
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
            method = RequestMethod.POST,
            value = "/delete")
    public ResponseEntity<?> deleteOne(@RequestParam Long accountId) {
        repository.delete(accountId);

        return ResponseEntity.noContent().build();
    }

    // ------------------
    // ABOUT ONE ACCOUNT'S RESOURCES
    // ------------------

    @RequestMapping(
            method = RequestMethod.POST,
            value = "/get-properties-of-account")
    public Collection<ResourceRto> getAccountResources(@RequestParam Long accountId) {
        Account account = validator.validateAccount(accountId);
        return account.getConnectedResources()
                .stream()
                .map(r -> ResourceMapper.INSTANCE.resourceToResourceRto(r))
                .collect(Collectors.toList());

    }

}
