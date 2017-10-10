/*
 * Created on Oct 10, 2017
 *
 * author age
 */
package com.alxgrk.level3.hateoas.mediatype.json;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.springframework.http.HttpMethod;

import com.alxgrk.level3.hateoas.resources.AccountResource;
import com.alxgrk.level3.models.Account;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;

public class ResourcesWithMethods0Test {

    private static final String HREF = "http://localhost:8080/";

    @Test
    public void testSerialisation() throws Exception {
        AccountResource accountResource = new AccountResource(new Account("foo", "bar"));
        LinkWithMethod link = new LinkWithMethod(HREF, "foo", HttpMethod.GET);

        ResourcesWithMethods<AccountResource> uut = new ResourcesWithMethods<>(
                Lists.newArrayList(accountResource), link);
        String actualJson = new ObjectMapper().writeValueAsString(uut);

        assertThat(actualJson).isEqualTo(
                "{\"_links\":[{\"rel\":\"foo\",\"href\":\"" + HREF + "\",\"method\":\"GET\"}],"
                        + "\"members\":[{\"id\":null,\"username\":\"foo\",\"surname\":null,\"name\":null,\"_links\":[]}]}");
    }
}
