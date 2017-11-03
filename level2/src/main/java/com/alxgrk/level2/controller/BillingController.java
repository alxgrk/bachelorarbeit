package com.alxgrk.level2.controller;

import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alxgrk.level2.repos.BillingRepository;
import com.alxgrk.level2.rest.mapping.BillMapper;
import com.alxgrk.level2.rest.rto.BillRto;

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

    @Autowired
    private final BillMapper mapper;

    // -------------------
    // ABOUT ALL RESOURCES
    // -------------------

    @RequestMapping(method = RequestMethod.GET)
    public Collection<BillRto> getAll() {
        return billingRepository.findAll()
                .stream()
                .map(mapper::billToBillRto)
                .collect(Collectors.toList());
    }

}
