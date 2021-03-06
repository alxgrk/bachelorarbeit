package com.alxgrk.bachelorarbeit.accounts.collection;

import com.alxgrk.bachelorarbeit.accounts.Account;
import com.alxgrk.bachelorarbeit.hateoas.Link;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collection;
import java.util.List;

import lombok.Data;

@Data
public class AccountCollection {

    @JsonProperty("_links")
    List<Link> links;

    @JsonProperty("members")
    Collection<Account> members;

}
