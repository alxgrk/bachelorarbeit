/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alxgrk.level3.error.advice;

import org.springframework.hateoas.VndErrors;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.alxgrk.level3.error.exceptions.BookingFailedException;
import com.alxgrk.level3.error.exceptions.ResourceNotFoundException;

/**
 * @author Josh Long
 */
// tag::code[]
@ControllerAdvice
class ResourceControllerAdvice {

    @ResponseBody
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    VndErrors resourceNotFoundExceptionHandler(ResourceNotFoundException ex) {
        return new VndErrors("error", ex.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(BookingFailedException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    VndErrors bookingFailedExceptionHandler(BookingFailedException ex) {
        return new VndErrors("error", ex.getMessage());
    }
}
// end::code[]