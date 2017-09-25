/*
 * Created on Sep 5, 2017
 *
 * author age
 */
package com.alxgrk.level3.error;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.VndErrors.VndError;

public class AlreadyExistsError extends VndError {

    public AlreadyExistsError(Object resource, Link linkToExistingObject) {
        super("error", resource + " already exists", linkToExistingObject);
    }

}
