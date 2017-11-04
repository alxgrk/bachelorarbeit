/*
 * Created on Sep 7, 2017
 *
 * author age
 */
package com.alxgrk.level3.hateoas;

import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.List;

import javax.transaction.Transactional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.alxgrk.level3.Level3Application;
import com.alxgrk.level3.hateoas.mediatype.MediaTypes;
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
        String entryPoint = "/";
        String firstHref = "/properties";
        String secondHref = "/properties/" + resId + "/administrators";
        String thirdHref = "/properties/" + resId + "/administrators";
        String fourthHref = "/properties/" + resId + "/administrators/" + idTwo;

        mockMvc.perform(get(entryPoint))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaTypes.ROOT_TYPE + ";charset=UTF-8"))
                .andExpect(jsonPath("$._links[3].rel")
                        .value(attachAndDetachResourceToUserJourney.get(0)));

        mockMvc.perform(get(firstHref))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaTypes.RESOURCE_TYPE + ";charset=UTF-8"))
                .andExpect(jsonPath("$.members[0].name").value(resName))
                .andExpect(jsonPath("$.members[0]._links[1].rel")
                        .value(attachAndDetachResourceToUserJourney.get(1)));

        mockMvc.perform(get(secondHref))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaTypes.ACCOUNT_TYPE + ";charset=UTF-8"))
                .andExpect(jsonPath("$._links[1].rel")
                        .value(attachAndDetachResourceToUserJourney.get(2)));

        mockMvc.perform(post(thirdHref).param("username", userNameTwo))
                .andExpect(status().isCreated());

        mockMvc.perform(get(secondHref))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaTypes.ACCOUNT_TYPE + ";charset=UTF-8"))
                .andExpect(jsonPath("$.members[0].username").value(userNameTwo))
                .andExpect(jsonPath("$.members[0]._links[1].rel")
                        .value(attachAndDetachResourceToUserJourney.get(3)));

        mockMvc.perform(delete(fourthHref))
                .andExpect(status().isNoContent());
    }

}
