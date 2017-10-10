/*
 * Created on Oct 10, 2017
 *
 * author age
 */
package com.alxgrk.level3.hateoas.mediatype.json;

import static org.assertj.core.api.Assertions.*;

import java.util.Map;

import org.assertj.core.util.Maps;
import org.junit.Before;
import org.junit.Test;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpMethod;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;

public class ResourceSupportWithMethods0Test {

    private static final String HREF = "http://localhost:8080/";

    private ResourceSupportWithMethods uut;

    @Before
    public void setUp() throws Exception {
        uut = new ResourceSupportWithMethods();
    }

    @Test
    public void testAddOneLinkWithMethod() throws Exception {
        Link link = new Link(HREF, "foo");

        uut.add(link, HttpMethod.GET);

        assertThat(uut.getLink("foo"))
                .hasFieldOrPropertyWithValue("href", HREF)
                .hasFieldOrPropertyWithValue("rel", "foo")
                .hasFieldOrPropertyWithValue("method", "GET");
    }

    @Test
    public void testAddTwoLinksWithMethods_map() throws Exception {
        Link link = new Link(HREF, "foo");
        Link link2 = new Link(HREF, "bar");
        Map<Link, HttpMethod> linkMap = Maps.newHashMap(link, HttpMethod.GET);
        linkMap.put(link2, HttpMethod.POST);

        uut.add(linkMap);

        assertThat(uut.getLink("foo"))
                .hasFieldOrPropertyWithValue("href", HREF)
                .hasFieldOrPropertyWithValue("rel", "foo")
                .hasFieldOrPropertyWithValue("method", "GET");

        assertThat(uut.getLink("bar"))
                .hasFieldOrPropertyWithValue("href", HREF)
                .hasFieldOrPropertyWithValue("rel", "bar")
                .hasFieldOrPropertyWithValue("method", "POST");
    }

    @Test
    public void testAddTwoLinksWithMethods_varargs() throws Exception {
        Link link = new Link(HREF, "foo");
        Link link2 = new Link(HREF, "bar");

        uut.add(new LinkWithMethod(link, HttpMethod.GET),
                new LinkWithMethod(link2, HttpMethod.POST));

        assertThat(uut.getLink("foo"))
                .hasFieldOrPropertyWithValue("href", HREF)
                .hasFieldOrPropertyWithValue("rel", "foo")
                .hasFieldOrPropertyWithValue("method", "GET");

        assertThat(uut.getLink("bar"))
                .hasFieldOrPropertyWithValue("href", HREF)
                .hasFieldOrPropertyWithValue("rel", "bar")
                .hasFieldOrPropertyWithValue("method", "POST");
    }

    @Test
    public void testSerialisation() throws Exception {
        Link link = new Link(HREF, "foo");

        uut.add(link, HttpMethod.GET);
        String actualJson = new ObjectMapper().writeValueAsString(uut);

        assertThat(actualJson).isEqualTo(
                "{\"_links\":[{\"rel\":\"foo\",\"href\":\"http://localhost:8080/\",\"method\":\"GET\"}]}");
    }

    @Test
    public void testUnsupportedMethods() throws Exception {
        Link link = new Link(HREF);
        Link link2 = new Link(HREF);

        assertThatThrownBy(() -> uut.getLinks())
                .isInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(() -> uut.add(link))
                .isInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(() -> uut.add(Lists.newArrayList(link)))
                .isInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(() -> uut.add(link, link2))
                .isInstanceOf(UnsupportedOperationException.class);
    }
}
