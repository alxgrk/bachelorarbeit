package com.alxgrk.level2.controller;

import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.io.IOException;
import java.util.Arrays;

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

import com.alxgrk.level2.Level2Application;
import com.alxgrk.level2.models.Account;
import com.alxgrk.level2.models.Organization;
import com.alxgrk.level2.models.Resource;
import com.alxgrk.level2.repos.AccountRepository;
import com.alxgrk.level2.repos.OrganizationRepository;
import com.alxgrk.level2.repos.ResourceRepository;
import com.google.common.collect.Sets;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Level2Application.class)
@WebAppConfiguration
@ActiveProfiles("test")
public class AccountControllerITest {

    private MediaType contentTypeJson = MediaType.APPLICATION_JSON_UTF8;

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

        Organization org = new Organization().setName("org");
        Resource res = new Resource().setName("res");
        organizationRepository.save(org);
        resourceRepository.save(res);

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
    public void testAccountsFound() throws Exception {
        mockMvc.perform(get("/accounts"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentTypeJson))
                .andExpect(jsonPath("$[0].id").value(idOne))
                .andExpect(jsonPath("$[0].surname").value(userNameOne))
                .andExpect(jsonPath("$[0].name").value(userNameOne))
                .andExpect(jsonPath("$[0].username").value(userNameOne))
                .andExpect(jsonPath("$[1].id").value(idTwo))
                .andExpect(jsonPath("$[1].surname").value(userNameTwo))
                .andExpect(jsonPath("$[1].name").value(userNameTwo))
                .andExpect(jsonPath("$[1].username").value(userNameTwo));
    }

    @Test
    @Transactional
    public void testAccountFound() throws Exception {
        mockMvc.perform(get("/accounts/" + idOne))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentTypeJson))
                .andExpect(jsonPath("$.id").value(idOne))
                .andExpect(jsonPath("$.surname").value(userNameOne))
                .andExpect(jsonPath("$.name").value(userNameOne))
                .andExpect(jsonPath("$.username").value(userNameOne));
    }

    @Test
    @Transactional
    public void testAccountCreated() throws Exception {
        mockMvc.perform(post("/accounts").contentType(contentTypeJson)
                .content("{"
                        + "     \"name\": \"test\","
                        + "     \"surname\": \"user\","
                        + "     \"username\": \"test\","
                        + "     \"password\": \"password\""
                        + "}"))
                .andExpect(status().isCreated());
    }

    @Test
    @Transactional
    public void testAccountConflictWithAlreadyExisting() throws Exception {
        mockMvc.perform(post("/accounts").contentType(contentTypeJson)
                .content("{"
                        + "     \"name\": \"test\","
                        + "     \"surname\": \"user\","
                        + "     \"username\": \"" + userNameOne + "\","
                        + "     \"password\": \"password\""
                        + "}"))
                .andExpect(status().isConflict());
    }

    @Test
    @Transactional
    public void testAccountUpdated() throws Exception {
        mockMvc.perform(put("/accounts/" + idOne).contentType(contentTypeJson)
                .content("{"
                        + "     \"name\": \"test\","
                        + "     \"surname\": \"user\","
                        + "     \"username\": \"test\""
                        + "}"))
                .andExpect(status().isCreated());
    }

    @Test
    @Transactional
    public void testAccountNotFound() throws Exception {
        mockMvc.perform(put("/accounts/" + 123456).contentType(contentTypeJson)
                .content("{"
                        + "     \"name\": \"test\","
                        + "     \"surname\": \"user\","
                        + "     \"username\": \"test\""
                        + "}"))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void testAccountDeleted() throws Exception {
        mockMvc.perform(delete("/accounts/" + idOne))
                .andExpect(status().isNoContent());
    }

    @Test
    @Transactional
    public void testAccountResourcesFound() throws Exception {
        mockMvc.perform(get("/accounts/" + idOne + "/resources"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentTypeJson))
                .andExpect(jsonPath("$[0].id").isNumber())
                .andExpect(jsonPath("$[0].name").value("res"))
                .andExpect(jsonPath("$[0].bookedTimeslots")
                        .isArray())
                .andExpect(jsonPath("$[0].availableTimeslots")
                        .isArray());
    }

    @SuppressWarnings("unchecked")
    protected String json(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(o, MediaType.APPLICATION_JSON,
                mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }
}
