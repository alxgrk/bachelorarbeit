package com.alxgrk.level3.hateoas.mediatype.json;

import java.util.Arrays;
import java.util.Collection;

import org.springframework.hateoas.Resources;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ResourcesWithMethods<RESOURCE> extends ResourceSupportWithMethods {

    @JsonIgnore
    private final Resources<RESOURCE> resources;

    public ResourcesWithMethods(Iterable<RESOURCE> content, LinkWithMethod... linksWithMethods) {
        this(content, Arrays.asList(linksWithMethods));
    }

    public ResourcesWithMethods(Iterable<RESOURCE> content,
            Collection<LinkWithMethod> linksWithMethods) {
        resources = new Resources<>(content);

        LinkWithMethod[] asArray = linksWithMethods
                .toArray(new LinkWithMethod[linksWithMethods.size()]);
        this.add(asArray);
    }

    @JsonProperty("members")
    public Collection<RESOURCE> getMembers() {
        return resources.getContent();
    }
}