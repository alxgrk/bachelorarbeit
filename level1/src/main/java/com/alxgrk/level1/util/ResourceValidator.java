/*
 * Created on May 22, 2017
 *
 * author age
 */
package com.alxgrk.level1.util;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.alxgrk.level1.error.exceptions.ResourceNotFoundException;
import com.alxgrk.level1.models.Resource;
import com.alxgrk.level1.repos.AccountRepository;
import com.alxgrk.level1.repos.ResourceRepository;

@Component
@Qualifier("resource")
public class ResourceValidator extends AccountValidator {

    private final ResourceRepository resourceRepo;

    public ResourceValidator(AccountRepository accountRepo, ResourceRepository resourceRepo) {
        super(accountRepo);

        this.resourceRepo = resourceRepo;
    }

    public Resource validateResource(Long resId) {
        Resource found = resourceRepo.findOne(resId);
        if (null == found)
            throw new ResourceNotFoundException(resId);
        else
            return found;
    }
}
