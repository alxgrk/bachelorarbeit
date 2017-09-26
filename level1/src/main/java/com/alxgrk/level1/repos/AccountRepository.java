/*
 * Created on May 16, 2017
 *
 * author age
 */
package com.alxgrk.level1.repos;

import java.util.Collection;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alxgrk.level1.models.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {

    // TODO think about this:
    // https://jaxenter.com/rest-api-spring-java-8-112289.html
    // @PostAuthorize("returnObject.username == principal.username or
    // hasRole('ROLE_ADMIN')")
    // @Override
    // Account findOne(Long accountId);

    Optional<Account> findByUsername(String userName);

    Optional<Account> findByName(String name);

    Collection<Account> findByOrganization(String orgId);

    Collection<Account> findByConnectedResources(String resId);
}
