package com.alxgrk.level3.hateoas.mediatype.json;

import org.springframework.hateoas.Link;
import org.springframework.http.HttpMethod;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

public class LinkWithMethod extends Link {

    @JsonProperty
    @Getter
    @Setter
    private String method;

    public LinkWithMethod(Link link, @NonNull HttpMethod method) {
        super(link.getHref(), link.getRel());
        this.method = method.toString();
    }

    public LinkWithMethod(String href, String rel, @NonNull HttpMethod method) {
        super(href, rel);
        this.method = method.toString();
    }

    @Override
    public String toString() {
        return super.toString() + ";method=" + method;
    }
}
