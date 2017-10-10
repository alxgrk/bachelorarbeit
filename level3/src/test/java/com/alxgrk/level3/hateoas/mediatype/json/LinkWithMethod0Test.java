/*
 * Created on Oct 10, 2017
 *
 * author age
 */
package com.alxgrk.level3.hateoas.mediatype.json;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpMethod;

import com.fasterxml.jackson.databind.ObjectMapper;

public class LinkWithMethod0Test {

    private static final String HREF = "http://localhost:8080/";

    @Test
    public void testConstructor() throws Exception {
        Link link = new Link(HREF);

        LinkWithMethod actual = new LinkWithMethod(link, HttpMethod.GET);
        String actualJson = new ObjectMapper().writeValueAsString(actual);

        assertThat(actualJson)
                .isEqualToIgnoringWhitespace("{\"rel\":\"self\",\"href\":\"" + HREF
                        + "\",\"method\":\"GET\"}");
    }

    @Test
    public void testToString() throws Exception {
        LinkWithMethod actual = new LinkWithMethod(HREF, "self", HttpMethod.GET);

        assertThat(actual.toString()).isEqualTo("<" + HREF + ">;rel=\"self\";method=GET");
    }

}
