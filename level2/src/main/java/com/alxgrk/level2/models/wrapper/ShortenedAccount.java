package com.alxgrk.level2.models.wrapper;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * This is a wrapper for an Account. It should only be used in conjunction with
 * the PUT method, which should not need a password.
 */
@Accessors(chain = true)
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class ShortenedAccount {

    @NonNull
    private String username;

    private String surname;

    private String name;

}
