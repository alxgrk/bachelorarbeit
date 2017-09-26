package com.alxgrk.level2.rest.rto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Data
public class AccountRto {

    @JsonProperty(access = Access.READ_ONLY)
    private Long id;

    private String username;

    @JsonProperty(access = Access.WRITE_ONLY)
    private String password;

    private String surname;

    private String name;

}
