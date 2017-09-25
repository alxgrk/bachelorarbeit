/*
 * Created on May 24, 2017
 *
 * author age
 */
package com.alxgrk.level3.controller;

import org.springframework.hateoas.Resources;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

public interface CollectionController<TYPE, RESOURCE> {

    Resources<?> getAll();

    ResponseEntity<?> addOne(@RequestBody TYPE input);

    RESOURCE getOne(@PathVariable Long id);

    ResponseEntity<?> updateOne(@PathVariable Long id,
            @RequestBody TYPE input);

    ResponseEntity<?> deleteOne(@PathVariable Long id);

}
