/*
 * Created on May 22, 2017
 *
 * author age
 */
package com.alxgrk.level2.util;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.alxgrk.level2.error.exceptions.OrgNotFoundException;
import com.alxgrk.level2.models.Organization;
import com.alxgrk.level2.repos.AccountRepository;
import com.alxgrk.level2.repos.OrganizationRepository;

@Component
@Qualifier("organization")
public class OrganizationValidator extends AccountValidator {

    private final OrganizationRepository orgRepo;

    public OrganizationValidator(AccountRepository accountRepo, OrganizationRepository orgRepo) {
        super(accountRepo);

        this.orgRepo = orgRepo;
    }

    public Organization validateOrganization(Long orgId) {
        Organization found = orgRepo.findOne(orgId);
        if (null == found)
            throw new OrgNotFoundException(orgId);
        else
            return found;
    }
}
