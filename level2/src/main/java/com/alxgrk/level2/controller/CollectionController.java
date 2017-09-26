/*
 * Created on May 24, 2017
 *
 * author age
 */
package com.alxgrk.level2.controller;

import java.util.Collection;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

public interface CollectionController<TYPE> {

    Collection<TYPE> getAll();

    ResponseEntity<?> addOne(@RequestBody TYPE input);

    TYPE getOne(@PathVariable Long id);

    ResponseEntity<?> updateOne(@PathVariable Long id,
            @RequestBody TYPE input);

    ResponseEntity<?> deleteOne(@PathVariable Long id);

}
