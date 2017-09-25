package com.alxgrk.level3.error.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class BookingFailedException extends RuntimeException {

    public BookingFailedException(Throwable e) {
        super("booking failed due to " + e.getClass()
                + ", see nested exception for more information", e);
    }
}
