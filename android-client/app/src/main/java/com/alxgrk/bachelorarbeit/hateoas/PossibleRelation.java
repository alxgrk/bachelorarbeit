package com.alxgrk.bachelorarbeit.hateoas;

/**
 * As all possible relations should be published by the server,
 * it is no problem to have a static list of these values.
 */
public enum PossibleRelation {

    SELF("self"),

    ACCOUNTS("accounts"),

    RESOURCES("resources"),

    ORGANIZATIONS("organizations"),

    ADMINISTRATORS("admins"),

    MEMBERS("members"),

    ORGANIZATION("organization"),

    DETACH("detach"),

    ATTACH("attach");

    private final String s;

    PossibleRelation(String s) {
        this.s = s;
    }

    @Override
    public String toString() {
        return s;
    }

}