/*
 * Created on Sep 7, 2017
 *
 * author age
 */
package com.alxgrk.level3.hateoas;

import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

import javax.transaction.Transactional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.alxgrk.level3.Level3Application;
import com.alxgrk.level3.hateoas.rels.Rels;
import com.alxgrk.level3.models.Account;
import com.alxgrk.level3.models.Organization;
import com.alxgrk.level3.models.Resource;
import com.alxgrk.level3.repos.AccountRepository;
import com.alxgrk.level3.repos.OrganizationRepository;
import com.alxgrk.level3.repos.ResourceRepository;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Level3Application.class)
@WebAppConfiguration
@ActiveProfiles("test")
public class JourneyTests {

    private static final List<String> attachAndDetachResourceToUserJourney = Lists.newArrayList(
            Rels.RESOURCES,
            Rels.ADMINISTRATORS,
            Rels.ATTACH,
            Rels.DETACH);

    private MediaType contentTypeHal = new MediaType("application", "hal+json",
            Charset.forName("utf8"));

    private MockMvc mockMvc;

    @SuppressWarnings("rawtypes")
    private HttpMessageConverter mappingJackson2HttpMessageConverter;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private ResourceRepository resourceRepository;

    private String password = "password";

    private String userNameOne = "testuser1";

    private String userNameTwo = "testuser2";

    private Long idOne;

    private Account accountOne;

    private Long idTwo;

    private Account accountTwo;

    private Organization org;

    private String orgName = "org";

    private Resource res;

    private String resName = "res";

    private Long orgId;

    private Long resId;

    @Autowired
    void setConverters(HttpMessageConverter<?>[] converters) {

        this.mappingJackson2HttpMessageConverter = Arrays.asList(converters).stream().filter(
                hmc -> hmc instanceof MappingJackson2HttpMessageConverter).findAny().orElse(null);

        assertNotNull("the JSON message converter must not be null",
                this.mappingJackson2HttpMessageConverter);
    }

    @Before
    public void setup() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();

        this.accountRepository.deleteAllInBatch();

        org = new Organization().setName(orgName);
        res = new Resource().setName(resName);
        org = organizationRepository.save(org);
        res = resourceRepository.save(res);
        orgId = org.getId();
        resId = res.getId();

        accountOne = new Account(userNameOne, password)
                .setSurname(userNameOne)
                .setName(userNameOne)
                .setOrganization(org)
                .setConnectedResources(Sets.newHashSet(res));
        accountOne = accountRepository.save(accountOne);
        idOne = accountOne.getId();

        accountTwo = new Account(userNameTwo, password)
                .setSurname(userNameTwo)
                .setName(userNameTwo);
        accountTwo = accountRepository.save(accountTwo);
        idTwo = accountTwo.getId();
    }

    @Test
    @Transactional
    public void testAttachAndDetachResourceToUserJourney() throws Exception {
        String prefix = "http://localhost";
        String entryPoint = "/";
        String firstHref = "/resources";
        String secondHref = "/resources/" + resId + "/administrators";
        String thirdHref = "/resources/" + resId + "/administrators";
        String fourthHref = "/resources/" + resId + "/administrators/" + idTwo;

        mockMvc.perform(get(entryPoint))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentTypeHal))
                .andExpect(jsonPath("$._links." + attachAndDetachResourceToUserJourney.get(0)
                        + ".href").value(prefix + firstHref));

        mockMvc.perform(get(firstHref))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentTypeHal))
                .andExpect(jsonPath("$._embedded.resources[0].name").value(resName))
                .andExpect(jsonPath("$._embedded.resources[0]._links."
                        + attachAndDetachResourceToUserJourney.get(1)
                        + ".href").value(prefix + secondHref));

        mockMvc.perform(get(secondHref))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentTypeHal))
                .andExpect(jsonPath("$._links." + attachAndDetachResourceToUserJourney.get(2)
                        + ".href").value(prefix + secondHref + "?username={username}"))
                .andExpect(jsonPath("$._links." + attachAndDetachResourceToUserJourney.get(2)
                        + ".templated").value("true"));

        mockMvc.perform(post(thirdHref).param("username", userNameTwo))
                .andExpect(status().isCreated());

        mockMvc.perform(get(secondHref))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentTypeHal))
                .andExpect(jsonPath("$._embedded.accounts[0].username").value(userNameTwo))
                .andExpect(jsonPath("$._embedded.accounts[0]._links."
                        + attachAndDetachResourceToUserJourney.get(3) + ".href")
                                .value(prefix + fourthHref));

        mockMvc.perform(delete(fourthHref))
                .andExpect(status().isNoContent());
    }

    @SuppressWarnings("unchecked")
    protected String json(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(o, MediaType.APPLICATION_JSON,
                mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }
}
