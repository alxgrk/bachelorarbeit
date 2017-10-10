package com.alxgrk.level3.hateoas.mediatype.json;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.http.HttpMethod;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

@ToString
public class ResourceSupportWithMethods extends ResourceSupport {

    @Getter
    @JsonProperty("_links")
    private final List<LinkWithMethod> linksWithMethods;

    public ResourceSupportWithMethods() {
        this.linksWithMethods = new ArrayList<LinkWithMethod>();
    }

    public void add(@NonNull Link link, @NonNull HttpMethod method) {
        LinkWithMethod linkWithMethod = new LinkWithMethod(link, method);
        this.linksWithMethods.add(linkWithMethod);
    }

    public void add(@NonNull Map<Link, HttpMethod> linksWithMethods) {
        for (Entry<Link, HttpMethod> candidate : linksWithMethods.entrySet()) {
            this.add(candidate.getKey(), candidate.getValue());
        }
    }

    public void add(@NonNull LinkWithMethod... linksWithMethods) {
        this.linksWithMethods.addAll(Arrays.asList(linksWithMethods));
    }

    @Override
    public Link getLink(String rel) {

        for (LinkWithMethod link : this.linksWithMethods) {
            if (link.getRel().equals(rel)) {
                return link;
            }
        }

        return null;
    }

    // --- FORBIDDEN OPERATIONS ---

    @JsonIgnore
    @Override
    public List<Link> getLinks() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(Link link) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(Iterable<Link> links) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(Link... links) {
        throw new UnsupportedOperationException();
    }
}