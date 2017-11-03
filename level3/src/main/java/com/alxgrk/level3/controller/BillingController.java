package com.alxgrk.level3.controller;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alxgrk.level3.hateoas.mediatype.MediaTypes;
import com.alxgrk.level3.hateoas.mediatype.json.ResourcesWithMethods;
import com.alxgrk.level3.hateoas.resources.BillingResource;
import com.alxgrk.level3.repos.BillingRepository;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Api(tags = "Billing", description = "managing payments")
@RestController
@RequestMapping("/billing")
@RequiredArgsConstructor
@Slf4j
public class BillingController {

    @Autowired
    private final BillingRepository billingRepository;

    // -------------------
    // ABOUT ALL RESOURCES
    // -------------------

    @RequestMapping(method = RequestMethod.GET, produces = MediaTypes.RESOURCE_TYPE)
    public ResourcesWithMethods<BillingResource> getAll() {
        List<BillingResource> billingResources = billingRepository.findAll()
                .stream()
                .map(BillingResource::new)
                .map((r) -> r.addSelfLink())
                .collect(Collectors.toList());

        ResourcesWithMethods<BillingResource> resources = new ResourcesWithMethods<>(
                billingResources);

        Link selfLink = linkTo(methodOn(BillingController.class).getAll())
                .withSelfRel();
        resources.add(selfLink, HttpMethod.GET);

        return resources;
    }

}
