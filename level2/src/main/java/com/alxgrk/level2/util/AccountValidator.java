/*
 * Created on May 22, 2017
 *
 * author age
 */
package com.alxgrk.level2.util;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.alxgrk.level2.error.exceptions.AccountNotFoundException;
import com.alxgrk.level2.models.Account;
import com.alxgrk.level2.repos.AccountRepository;

import lombok.RequiredArgsConstructor;

@Component
@Primary
@RequiredArgsConstructor
public class AccountValidator {

    private final AccountRepository accountRepo;

    public Account validateAccount(Long accountId) {
        Account found = accountRepo.findOne(accountId);
        if (null == found)
            throw new AccountNotFoundException(accountId);
        else
            return found;
    }

}
