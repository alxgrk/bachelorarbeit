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
import com.alxgrk.level1.error.exceptions.BookingFailedException;
import com.alxgrk.level1.error.exceptions.timeslot.NoAvailableTimeslotsException;
import com.alxgrk.level1.error.exceptions.timeslot.TimeslotsNotAvailableException;
import com.alxgrk.level1.error.exceptions.timeslot.TimeslotsToBookClashException;
import com.alxgrk.level1.models.Account;
import com.alxgrk.level1.models.Resource;
import com.alxgrk.level1.repos.AccountRepository;
import com.alxgrk.level1.repos.ResourceRepository;
import com.alxgrk.level1.rest.mapping.AccountMapper;
import com.alxgrk.level1.rest.mapping.ResourceMapper;
import com.alxgrk.level1.rest.rto.AccountRto;
import com.alxgrk.level1.rest.rto.ResourceRto;
import com.alxgrk.level1.util.ResourceValidator;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Api(tags = "Resources", description = "managing resources")
@RestController
@RequestMapping("/resources")
@RequiredArgsConstructor
@Slf4j
public class ResourceController implements CollectionController<ResourceRto> {

    @Autowired
    private final AccountRepository accountRepository;

    @Autowired
    private final ResourceRepository resourceRepository;

    @Qualifier("resource")
    @Autowired
    private final ResourceValidator validator;

    @Autowired
    private final ResourceMapper mapper;

    // -------------------
    // ABOUT ALL RESOURCES
    // -------------------

    @Override
    @RequestMapping(path = "/get-all", method = RequestMethod.POST)
    public Collection<ResourceRto> getAll() {
        return resourceRepository.findAll()
                .stream()
                .map(r -> mapper.resourceToResourceRto(r))
                .collect(Collectors.toList());

    }

    // ------------------
    // ABOUT ONE RESOURCE
    // ------------------

    @Override
    @RequestMapping(path = "/create", method = RequestMethod.POST)
    public ResponseEntity<?> addOne(@RequestBody ResourceRto input) {
        Optional<Resource> resourceOptional = resourceRepository.findByName(input
                .getName());

        if (resourceOptional.isPresent()) {
            Resource resource = resourceOptional.get();

            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new AlreadyExistsError(resource));
        } else {
            try {
                Resource newResource = mapper.resourceRtoToResource(input);

                resourceRepository.save(newResource);

                return ResponseEntity.created(URI.create("http://localhost:8080/resources/"
                        + newResource.getId())).build();
            } catch (NoAvailableTimeslotsException | TimeslotsNotAvailableException
                    | TimeslotsToBookClashException e) {
                log.warn("Trying to create a resource failed with the following reason: {}", e);
                throw new BookingFailedException(e);
            }
        }
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "/get-one")
    public ResourceRto getOne(@RequestParam Long resId) {
        Resource resource = validator.validateResource(resId);

        return mapper.resourceToResourceRto(resource);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "/update")
    public ResponseEntity<?> updateOne(@RequestParam Long resId,
            @RequestBody ResourceRto input) {
        Resource resource = validator.validateResource(resId);

        try {
            mapper.updateResourceFromResourceRto(input, resource);

            resourceRepository.save(resource);

            return ResponseEntity.created(URI.create("http://localhost:8080/resources/" + resource
                    .getId())).build();
        } catch (NoAvailableTimeslotsException | TimeslotsNotAvailableException
                | TimeslotsToBookClashException e) {
            log.warn("Trying to create a resource failed with the following reason: {}", e);
            throw new BookingFailedException(e);
        }
    }

    @Override
    @RequestMapping(
            method = RequestMethod.POST,
            value = "/delete")
    public ResponseEntity<?> deleteOne(@RequestParam Long resId) {
        resourceRepository.delete(resId);

        return ResponseEntity.noContent().build();
    }

    // -------------------------------
    // ABOUT RESOURCE'S ADMINISTRATORS
    // -------------------------------

    @RequestMapping(
            method = RequestMethod.POST,
            value = "/get-administrators-of-resource")
    public Collection<AccountRto> getAllAdministrators(@RequestParam Long resId) {
        Resource resource = validator.validateResource(resId);

        return resource.getAdministrators()
                .stream()
                .map(a -> AccountMapper.INSTANCE.accountToAccountRto(a))
                .collect(Collectors.toList());
    }

    @RequestMapping(
            method = RequestMethod.POST,
            value = "/add-administrator-to-resource")
    @Transactional
    public ResponseEntity<?> attachMemberToResource(@RequestParam Long resId,
            @RequestParam("username") String username) {
        Resource resource = validator.validateResource(resId);

        Optional<Account> accountOptional = accountRepository.findByUsername(username);

        if (!accountOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        } else {
            Account account = accountOptional.get();
            Set<Resource> connectedResources = account.getConnectedResources();
            connectedResources.add(resource);
            account.setConnectedResources(connectedResources);

            Set<Account> administrators = resource.getAdministrators();
            administrators.add(account);
            resource.setAdministrators(administrators);

            return ResponseEntity.created(URI.create("http://localhost:8080/resources/" + resource
                    .getId() + "/administrators")).build();
        }
    }

    @RequestMapping(
            method = RequestMethod.POST,
            value = "/delete-administrator-from-resource")
    @Transactional
    public ResponseEntity<?> detachMemberFromResource(@RequestParam Long resId,
            @RequestParam Long adminId) {
        Resource resource = validator.validateResource(resId);

        Account account = validator.validateAccount(adminId);
        Set<Resource> connectedResources = account.getConnectedResources();
        connectedResources.remove(resource);
        account.setConnectedResources(connectedResources);

        Set<Account> admins = resource.getAdministrators();
        admins.remove(account);
        resource.setAdministrators(admins);

        return ResponseEntity.noContent()
                .location(URI.create("http://localhost:8080/resources/" + resource
                        .getId() + "/administrators"))
                .build();
    }
}
