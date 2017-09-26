package com.alxgrk.level1.controller;

import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

import com.alxgrk.level1.Level1Application;
import com.alxgrk.level1.models.Account;
import com.alxgrk.level1.models.Organization;
import com.alxgrk.level1.repos.AccountRepository;
import com.alxgrk.level1.repos.OrganizationRepository;
import com.google.common.collect.Sets;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Level1Application.class)
@WebAppConfiguration
@ActiveProfiles("test")
public class OrganizationControllerITest {

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

    private String username = "jlong";

    private String password = "password";

    private Long accountId;

    private String orgNameOne = "MyOrg1";

    private Long idOne;

    private Organization organizationOne;

    private String orgNameTwo = "MyOrg2";

    private Long idTwo;

    private Organization organizationTwo;

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
        Account account = accountRepository.save(
                new Account(username, password).setName(username));
        accountId = account.getId();

        this.organizationRepository.deleteAll();

        organizationOne = new Organization()
                .setName(orgNameOne)
                .setMembers(Sets.newHashSet(account));
        organizationOne = organizationRepository.save(organizationOne);
        idOne = organizationOne.getId();

        accountRepository.save(account.setOrganization(organizationOne));

        organizationTwo = new Organization()
                .setName(orgNameTwo);
        organizationTwo = organizationRepository.save(organizationTwo);
        idTwo = organizationTwo.getId();
    }

    @Test
    @Transactional
    public void testOrganizationsFound() throws Exception {
        mockMvc.perform(post("/orgs/get-all"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentTypeJson))
                .andExpect(jsonPath("$[0].id").value(idOne))
                .andExpect(jsonPath("$[0].name").value(
                        orgNameOne))
                .andExpect(jsonPath("$[1].id").value(idTwo))
                .andExpect(jsonPath("$[1].name").value(
                        orgNameTwo));
    }

    @Test
    @Transactional
    public void testOrganizationFound() throws Exception {
        mockMvc.perform(post("/orgs/get-one").param("orgId", idOne.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentTypeJson))
                .andExpect(jsonPath("$.id").value(idOne))
                .andExpect(jsonPath("$.name").value(orgNameOne));
    }

    @Test
    @Transactional
    public void testOrganizationCreated() throws Exception {
        mockMvc.perform(post("/orgs/create").contentType(contentTypeJson)
                .content("{"
                        + "     \"name\": \"test\""
                        + "}"))
                .andExpect(status().isCreated());
    }

    @Test
    @Transactional
    public void testOrganizationConflictWithAlreadyExisting() throws Exception {
        mockMvc.perform(post("/orgs/create").contentType(contentTypeJson)
                .content("{"
                        + "     \"name\": \"" + orgNameOne + "\""
                        + "}"))
                .andExpect(status().isConflict());
    }

    @Test
    @Transactional
    public void testOrganizationUpdated() throws Exception {
        mockMvc.perform(post("/orgs/update").param("orgId", idOne.toString())
                .contentType(contentTypeJson)
                .content("{"
                        + "     \"name\": \"test\""
                        + "}"))
                .andExpect(status().isCreated());
    }

    @Test
    @Transactional
    public void testOrganizationNotFound() throws Exception {
        mockMvc.perform(post("/orgs/update").param("orgId", "123456")
                .contentType(contentTypeJson)
                .content("{"
                        + "     \"name\": \"test\""
                        + "}"))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void testOrganizationDeleted() throws Exception {
        mockMvc.perform(post("/orgs/delete").param("orgId", idOne.toString()))
                .andExpect(status().isNoContent());
    }

    @Test
    @Transactional
    public void testOrganizationMembersFound() throws Exception {
        mockMvc.perform(post("/orgs/get-members-of-org").param("orgId", idOne.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentTypeJson))
                .andExpect(jsonPath("$[0].id").isNumber())
                .andExpect(jsonPath("$[0].surname").isEmpty())
                .andExpect(jsonPath("$[0].name").value(username))
                .andExpect(jsonPath("$[0].username").value(username));
    }

    @Test
    @Transactional
    public void testOrganizationMemberAttached() throws Exception {
        mockMvc.perform(post("/orgs/add-member-to-org").param("orgId", idOne.toString())
                .param("username", username))
                .andExpect(status().isCreated());
    }

    @Test
    @Transactional
    public void testOrganizationMemberAttachNotExisting() throws Exception {
        mockMvc.perform(post("/orgs/add-member-to-org").param("orgId", idOne.toString())
                .param("username", "agdjiojdft"))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void testOrganizationMemberDetached() throws Exception {
        mockMvc.perform(post("/orgs/delete-member-from-org")
                .param("orgId", idOne.toString())
                .param("accountId", accountId.toString()))
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
