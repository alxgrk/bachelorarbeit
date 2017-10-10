package com.alxgrk.level3.controller;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;
import static org.springframework.http.HttpMethod.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alxgrk.level3.error.AlreadyExistsError;
import com.alxgrk.level3.error.exceptions.BookingFailedException;
import com.alxgrk.level3.error.exceptions.timeslot.NoAvailableTimeslotsException;
import com.alxgrk.level3.error.exceptions.timeslot.TimeslotsNotAvailableException;
import com.alxgrk.level3.error.exceptions.timeslot.TimeslotsToBookClashException;
import com.alxgrk.level3.hateoas.mapping.ResourceMapper;
import com.alxgrk.level3.hateoas.mediatype.MediaTypes;
import com.alxgrk.level3.hateoas.mediatype.json.LinkWithMethod;
import com.alxgrk.level3.hateoas.mediatype.json.ResourcesWithMethods;
import com.alxgrk.level3.hateoas.rels.Rels;
import com.alxgrk.level3.hateoas.resources.AccountResource;
import com.alxgrk.level3.hateoas.resources.ResourceResource;
import com.alxgrk.level3.hateoas.resources.ResourcesWithLinks;
import com.alxgrk.level3.hateoas.rto.ResourceRto;
import com.alxgrk.level3.models.Account;
import com.alxgrk.level3.models.Resource;
import com.alxgrk.level3.repos.AccountRepository;
import com.alxgrk.level3.repos.ResourceRepository;
import com.alxgrk.level3.util.ResourceValidator;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Api(tags = "Resources", description = "managing resources")
@RestController
@RequestMapping("/resources")
@RequiredArgsConstructor
@Slf4j
public class ResourceController implements CollectionController<ResourceRto, ResourceResource> {

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
    @RequestMapping(method = RequestMethod.GET, produces = MediaTypes.RESOURCE_TYPE)
    public ResourcesWithMethods<ResourceResource> getAll() {
        List<ResourceResource> resourceResources = resourceRepository.findAll()
                .stream()
                .map(ResourceResource::new)
                .map((r) -> r.addSelfLink()
                        .addAdministratorsLink())
                .collect(Collectors.toList());

        return new ResourcesWithLinks<>(resourceResources, this)
                .addSelfLink().create();
    }

    // ------------------
    // ABOUT ONE RESOURCE
    // ------------------

    @Override
    @RequestMapping(method = RequestMethod.POST, consumes = MediaTypes.RESOURCE_TYPE)
    public ResponseEntity<?> addOne(@RequestBody ResourceRto input) {
        Optional<Resource> resourceOptional = resourceRepository.findByName(input
                .getName());

        if (resourceOptional.isPresent()) {
            Resource resource = resourceOptional.get();
            Link selfLink = new ResourceResource(resource)
                    .addSelfLink()
                    .getLink(Link.REL_SELF);

            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new AlreadyExistsError(resource, selfLink));
        } else {
            try {
                Resource newResource = mapper.resourceRtoToResource(input);

                resourceRepository.save(newResource);

                Link forOneAccount = new ResourceResource(newResource)
                        .addSelfLink()
                        .getLink(Link.REL_SELF);

                return ResponseEntity.created(URI.create(forOneAccount.getHref())).build();
            } catch (NoAvailableTimeslotsException | TimeslotsNotAvailableException
                    | TimeslotsToBookClashException e) {
                log.warn("Trying to create a resource failed with the following reason: {}", e);
                throw new BookingFailedException(e);
            }
        }
    }

    @Override
    @RequestMapping(
            method = RequestMethod.GET,
            value = "/{resId}",
            produces = MediaTypes.RESOURCE_TYPE)
    public ResourceResource getOne(@PathVariable Long resId) {
        Resource resource = validator.validateResource(resId);

        ResourceResource resourceResource = new ResourceResource(resource)
                .addSelfLink()
                .addAdministratorsLink();

        return resourceResource;
    }

    @Override
    @RequestMapping(
            method = RequestMethod.PUT,
            value = "/{resId}",
            consumes = MediaTypes.RESOURCE_TYPE)
    public ResponseEntity<?> updateOne(@PathVariable Long resId,
            @RequestBody ResourceRto input) {
        Resource resource = validator.validateResource(resId);

        try {
            mapper.updateResourceFromResourceRto(input, resource);

            resourceRepository.save(resource);

            Link forOneResource = new ResourceResource(resource)
                    .addSelfLink()
                    .getLink(Link.REL_SELF);

            return ResponseEntity.created(URI.create(forOneResource.getHref())).build();
        } catch (NoAvailableTimeslotsException | TimeslotsNotAvailableException
                | TimeslotsToBookClashException e) {
            log.warn("Trying to create a resource failed with the following reason: {}", e);
            throw new BookingFailedException(e);
        }
    }

    @Override
    @RequestMapping(
            method = RequestMethod.DELETE,
            value = "/{resId}")
    public ResponseEntity<?> deleteOne(@PathVariable Long resId) {
        resourceRepository.delete(resId);

        return ResponseEntity.noContent().build();
    }

    // -------------------------------
    // ABOUT RESOURCE'S ADMINISTRATORS
    // -------------------------------

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/{resId}/administrators",
            produces = MediaTypes.ACCOUNT_TYPE)
    public ResourcesWithMethods<AccountResource> getAllAdministrators(@PathVariable Long resId) {
        Resource resource = validator.validateResource(resId);

        // prepare each account
        List<AccountResource> accountResources = resource.getAdministrators()
                .stream()
                .map(AccountResource::new)
                .map((r) -> {
                    Link detachLink = linkTo(methodOn(ResourceController.class)
                            .detachMemberFromResource(resId, r.getAccount().getId()))
                                    .withRel(Rels.DETACH);

                    return r.addSelfLink()
                            .addLinks(new LinkWithMethod(detachLink, DELETE));
                })
                .collect(Collectors.toList());

        // prepare the surrounding resource

        Link attachLink = linkTo(methodOn(ResourceController.class)
                .attachMemberToResource(resId, null))
                        .withRel(Rels.ATTACH);

        return new ResourcesWithLinks<>(accountResources, this)
                .addSelfLink()
                .addCustomLinks(new LinkWithMethod(attachLink, POST))
                .create();
    }

    @RequestMapping(
            method = RequestMethod.POST,
            value = "/{resId}/administrators")
    @Transactional
    public ResponseEntity<?> attachMemberToResource(@PathVariable Long resId,
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

            Link selfLink = new ResourceResource(resource)
                    .addAdministratorsLink()
                    .getLink(Rels.ADMINISTRATORS);
            return ResponseEntity.created(URI.create(selfLink.getHref())).build();
        }
    }

    @RequestMapping(
            method = RequestMethod.DELETE,
            value = "/{resId}/administrators/{adminId}")
    @Transactional
    public ResponseEntity<?> detachMemberFromResource(@PathVariable Long resId,
            @PathVariable Long adminId) {
        Resource resource = validator.validateResource(resId);

        Account account = validator.validateAccount(adminId);
        Set<Resource> connectedResources = account.getConnectedResources();
        connectedResources.remove(resource);
        account.setConnectedResources(connectedResources);

        Set<Account> admins = resource.getAdministrators();
        admins.remove(account);
        resource.setAdministrators(admins);

        Link selfLink = new ResourceResource(resource)
                .addAdministratorsLink()
                .getLink(Rels.ADMINISTRATORS);
        return ResponseEntity.noContent()
                .location(URI.create(selfLink.getHref()))
                .build();
    }
}
