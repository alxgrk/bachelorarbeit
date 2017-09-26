/*
 * Created on Sep 5, 2017
 *
 * author age
 */
package com.alxgrk.level2.error;

public class AlreadyExistsError extends Exception {

    public AlreadyExistsError(Object resource) {
        super(resource + " already exists");
    }

}
