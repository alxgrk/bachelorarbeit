/*
 * Created on May 23, 2017
 *
 * author age
 */
package com.alxgrk.level3.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alxgrk.level3.hateoas.mediatype.MediaTypes;
import com.alxgrk.level3.hateoas.resources.RootResource;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;

@Api(tags = "# Entry", description = "the entry point for your journey")
@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class RootController {

    @RequestMapping(method = RequestMethod.GET, produces = MediaTypes.ROOT_TYPE)
    public RootResource getRoot() {
        return new RootResource();
    }
}
