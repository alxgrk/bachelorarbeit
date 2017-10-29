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

    ATTACH("attach"),

    CREATE("create"),

    UPDATE("update"),

    DELETE("delete");

    private final String s;

    PossibleRelation(String s) {
        this.s = s;
    }

    public static PossibleRelation getBy(String value) {
        for (PossibleRelation v : values())
            if (v.toString().equalsIgnoreCase(value))
                return v;
        throw new IllegalArgumentException();
    }

    @Override
    public String toString() {
        return s;
    }
}
