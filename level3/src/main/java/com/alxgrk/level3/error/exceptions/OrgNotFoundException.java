package com.alxgrk.level3.error.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class OrgNotFoundException extends RuntimeException {

    public OrgNotFoundException(Long orgId) {
        super("could not find organization '" + orgId + "'.");
    }
}
