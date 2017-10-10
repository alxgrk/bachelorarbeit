package com.alxgrk.level3.controller;

import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;

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
import com.alxgrk.level3.models.Account;
import com.alxgrk.level3.models.Resource;
import com.alxgrk.level3.repos.AccountRepository;
import com.alxgrk.level3.repos.ResourceRepository;
import com.google.common.collect.Sets;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Level3Application.class)
@WebAppConfiguration
@ActiveProfiles("test")
public class ResourceControllerITest {

    private MockMvc mockMvc;

    @SuppressWarnings("rawtypes")
    private HttpMessageConverter mappingJackson2HttpMessageConverter;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ResourceRepository resourceRepository;

    private String username = "jlong";

    private String password = "password";

    private Long accountId;

    private String resNameOne = "garage";

    private Long idOne;

    private Resource resourceOne;

    private String resNameTwo = "parkplatz";

    private Long idTwo;

    private Resource resourceTwo;

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

        this.resourceRepository.deleteAll();

        resourceOne = new Resource()
                .setName(resNameOne)
                .setAdministrators(Sets.newHashSet(account));
        resourceOne = resourceRepository.save(resourceOne);
        idOne = resourceOne.getId();

        resourceTwo = new Resource()
                .setName(resNameTwo);
        resourceTwo = resourceRepository.save(resourceTwo);
        idTwo = resourceTwo.getId();
    }

    @Test
    @Transactional
    public void testResourcesFound() throws Exception {
        mockMvc.perform(get("/resources"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaTypes.RESOURCE_TYPE + ";charset=UTF-8"))
                .andExpect(jsonPath("$.members[0].id").value(idOne))
                .andExpect(jsonPath("$.members[0].name").value(
                        resNameOne))
                .andExpect(jsonPath("$.members[1].id").value(idTwo))
                .andExpect(jsonPath("$.members[1].name").value(
                        resNameTwo));
    }

    @Test
    @Transactional
    public void testResourceFound() throws Exception {
        mockMvc.perform(get("/resources/" + idOne))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaTypes.RESOURCE_TYPE + ";charset=UTF-8"))
                .andExpect(jsonPath("$.id").value(idOne))
                .andExpect(jsonPath("$.name").value(resNameOne));
    }

    @Test
    @Transactional
    public void testResourceCreated() throws Exception {
        mockMvc.perform(post("/resources").contentType(MediaTypes.RESOURCE_TYPE)
                .content("{"
                        + "     \"name\": \"test\","
                        + "     \"availableTimeslots\": ["
                        + "         {"
                        + "             \"beginning\": {"
                        + "                 \"dayOfMonth\": 9,"
                        + "                 \"year\": 2017,"
                        + "                 \"monthValue\": 4,"
                        + "                 \"hour\": 1,"
                        + "                 \"minute\": 0,"
                        + "                 \"second\": 0"
                        + "             },"
                        + "             \"ending\": {"
                        + "                 \"dayOfMonth\": 9,"
                        + "                 \"year\": 2018,"
                        + "                 \"monthValue\": 4,"
                        + "                 \"hour\": 1,"
                        + "                 \"minute\": 0,"
                        + "                 \"second\": 0"
                        + "             }"
                        + "         }"
                        + "      ]"
                        + "}"))
                .andExpect(status().isCreated());
    }

    @Test
    @Transactional
    public void testResourceCreatedWithMissingTimeslots() throws Exception {
        mockMvc.perform(post("/resources").contentType(MediaTypes.RESOURCE_TYPE)
                .content("{"
                        + "     \"name\": \"test\""
                        + "}"))
                .andExpect(status().isConflict());
    }

    @Test
    @Transactional
    public void testResourceConflictWithAlreadyExisting() throws Exception {
        mockMvc.perform(post("/resources").contentType(MediaTypes.RESOURCE_TYPE)
                .content("{"
                        + "     \"name\": \"" + resNameOne + "\""
                        + "}"))
                .andExpect(status().isConflict());
    }

    @Test
    @Transactional
    public void testResourceUpdated() throws Exception {
        mockMvc.perform(put("/resources/" + idOne).contentType(MediaTypes.RESOURCE_TYPE)
                .content("{"
                        + "     \"name\": \"test\","
                        + "     \"availableTimeslots\": ["
                        + "         {"
                        + "             \"beginning\": {"
                        + "                 \"dayOfMonth\": 9,"
                        + "                 \"year\": 2017,"
                        + "                 \"monthValue\": 4,"
                        + "                 \"hour\": 1,"
                        + "                 \"minute\": 0,"
                        + "                 \"second\": 0"
                        + "             },"
                        + "             \"ending\": {"
                        + "                 \"dayOfMonth\": 9,"
                        + "                 \"year\": 2018,"
                        + "                 \"monthValue\": 4,"
                        + "                 \"hour\": 1,"
                        + "                 \"minute\": 0,"
                        + "                 \"second\": 0"
                        + "             }"
                        + "         }"
                        + "      ]"
                        + "}"))
                .andExpect(status().isCreated());
    }

    @Test
    @Transactional
    public void testResourceUpdatedWithMissingTimeslots() throws Exception {
        // TODO mapper misuses setting booked timeslots by getting all timeslots
        // and calling .addAll() there
        mockMvc.perform(put("/resources/" + idOne).contentType(MediaTypes.RESOURCE_TYPE)
                .content("{"
                        + "     \"name\": \"test\","
                        + "     \"bookedTimeslots\": ["
                        + "         {"
                        + "             \"beginning\": {"
                        + "                 \"dayOfMonth\": 9,"
                        + "                 \"year\": 2017,"
                        + "                 \"monthValue\": 4,"
                        + "                 \"hour\": 1,"
                        + "                 \"minute\": 0,"
                        + "                 \"second\": 0"
                        + "             },"
                        + "             \"ending\": {"
                        + "                 \"dayOfMonth\": 9,"
                        + "                 \"year\": 2018,"
                        + "                 \"monthValue\": 4,"
                        + "                 \"hour\": 1,"
                        + "                 \"minute\": 0,"
                        + "                 \"second\": 0"
                        + "             }"
                        + "         }"
                        + "      ]"
                        + "}"))
                .andExpect(status().isConflict());
    }

    @Test
    @Transactional
    public void testResourceNotFound() throws Exception {
        mockMvc.perform(put("/resources/" + 123456).contentType(MediaTypes.RESOURCE_TYPE)
                .content("{"
                        + "     \"name\": \"test\""
                        + "}"))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void testResourceDeleted() throws Exception {
        mockMvc.perform(delete("/resources/" + idOne))
                .andExpect(status().isNoContent());
    }

    @Test
    @Transactional
    public void testResourceMembersFound() throws Exception {
        mockMvc.perform(get("/resources/" + idOne + "/administrators"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaTypes.ACCOUNT_TYPE + ";charset=UTF-8"))
                .andExpect(jsonPath("$.members[0].id").isNumber())
                .andExpect(jsonPath("$.members[0].surname").isEmpty())
                .andExpect(jsonPath("$.members[0].name").value(username))
                .andExpect(jsonPath("$.members[0].username").value(username));
    }

    @Test
    @Transactional
    public void testResourceMemberAttached() throws Exception {
        mockMvc.perform(post("/resources/" + idOne + "/administrators").param("username", username))
                .andExpect(status().isCreated());
    }

    @Test
    @Transactional
    public void testResourceMemberAttachNotExisting() throws Exception {
        mockMvc.perform(post("/resources/" + idOne + "/administrators").param("username",
                "agdjiojdft"))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void testResourceMemberDetached() throws Exception {
        mockMvc.perform(delete("/resources/" + idOne + "/administrators/" + accountId))
                .andExpect(status().isNoContent());
    }

}
