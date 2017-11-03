/*
 * Created on May 22, 2017
 *
 * author age
 */
package com.alxgrk.level3.hateoas.resources;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.core.Relation;
import org.springframework.http.HttpMethod;

import com.alxgrk.level3.controller.ResourceController;
import com.alxgrk.level3.hateoas.mapping.BillMapper;
import com.alxgrk.level3.hateoas.mediatype.json.LinkWithMethod;
import com.alxgrk.level3.hateoas.mediatype.json.ResourceSupportWithMethods;
import com.alxgrk.level3.hateoas.rto.BillRto;
import com.alxgrk.level3.models.Bill;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import lombok.Getter;

@Getter
@Relation(
        value = BillingResource.RESOURCE_NAME,
        collectionRelation = BillingResource.RESOURCE_NAME + 's')
public class BillingResource extends ResourceSupportWithMethods {

    public static final String RESOURCE_NAME = "billing";

    @JsonIgnore
    private final Bill bill;

    @JsonUnwrapped
    private final BillRto billRto;

    public BillingResource(Bill bill) {
        this.bill = bill;

        billRto = BillMapper.INSTANCE.billToBillRto(bill);
    }

    public BillingResource addSelfLink() {

        Link selfLink = linkTo(methodOn(ResourceController.class).getOne(bill.getId()))
                .withSelfRel();
        add(selfLink, HttpMethod.GET);

        return this;
    }

    /**
     * Just a delegate method, that return the resource itself.
     * 
     * @param links
     * @return
     */
    public BillingResource addLinks(LinkWithMethod... links) {

        add(links);

        return this;
    }
}
