package com.alxgrk.level1.controller;

import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.io.IOException;
import java.time.LocalDateTime;
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
import com.alxgrk.level1.models.Resource;
import com.alxgrk.level1.models.Timeslot;
import com.alxgrk.level1.repos.AccountRepository;
import com.alxgrk.level1.repos.ResourceRepository;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Level1Application.class)
@WebAppConfiguration
@ActiveProfiles("test")
public class ResourceControllerITest {

    private MediaType contentTypeJson = MediaType.APPLICATION_JSON_UTF8;

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

        Timeslot timeslot = new Timeslot(LocalDateTime.of(2000, 1, 1, 1, 1),
                LocalDateTime.of(2100, 1, 1, 1, 1));
        resourceOne = new Resource()
                .setName(resNameOne)
                .setAdministrators(Sets.newHashSet(account))
                .setAvailableTimeslots(Lists.newArrayList(timeslot));
        resourceOne = resourceRepository.save(resourceOne);
        idOne = resourceOne.getId();

        resourceTwo = new Resource()
                .setName(resNameTwo)
                .setAvailableTimeslots(Lists.newArrayList(timeslot));
        resourceTwo = resourceRepository.save(resourceTwo);
        idTwo = resourceTwo.getId();
    }

    @Test
    @Transactional
    public void testResourcesFound() throws Exception {
        mockMvc.perform(post("/resources/get-all"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentTypeJson))
                .andExpect(jsonPath("$[0].id").value(idOne))
                .andExpect(jsonPath("$[0].name").value(
                        resNameOne))
                .andExpect(jsonPath("$[1].id").value(idTwo))
                .andExpect(jsonPath("$[1].name").value(
                        resNameTwo));
    }

    @Test
    @Transactional
    public void testResourceFound() throws Exception {
        mockMvc.perform(post("/resources/get-one").param("resId", resourceOne.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentTypeJson))
                .andExpect(jsonPath("$.id").value(idOne))
                .andExpect(jsonPath("$.name").value(resNameOne));
    }

    @Test
    @Transactional
    public void testResourceCreated() throws Exception {
        mockMvc.perform(post("/resources/create").contentType(contentTypeJson)
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
        mockMvc.perform(post("/resources/create").contentType(contentTypeJson)
                .content("{"
                        + "     \"name\": \"test\""
                        + "}"))
                .andExpect(status().isConflict());
    }

    @Test
    @Transactional
    public void testResourceConflictWithAlreadyExisting() throws Exception {
        mockMvc.perform(post("/resources/create").contentType(contentTypeJson)
                .content("{"
                        + "     \"name\": \"" + resNameOne + "\""
                        + "}"))
                .andExpect(status().isConflict());
    }

    @Test
    @Transactional
    public void testResourceUpdated() throws Exception {
        mockMvc.perform(post("/resources/update").param("resId", resourceOne.getId().toString())
                .contentType(contentTypeJson)
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
        mockMvc.perform(post("/resources/update").param("resId", resourceOne.getId().toString())
                .contentType(contentTypeJson)
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
        mockMvc.perform(post("/resources/update").param("resId", "123456")
                .contentType(contentTypeJson)
                .content("{"
                        + "     \"name\": \"test\""
                        + "}"))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void testResourceDeleted() throws Exception {
        mockMvc.perform(post("/resources/delete").param("resId", resourceOne.getId().toString()))
                .andExpect(status().isNoContent());
    }

    @Test
    @Transactional
    public void testResourceMembersFound() throws Exception {
        mockMvc.perform(post("/resources/get-administrators-of-resource")
                .param("resId", resourceOne.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentTypeJson))
                .andExpect(jsonPath("$[0].id").isNumber())
                .andExpect(jsonPath("$[0].surname").isEmpty())
                .andExpect(jsonPath("$[0].name").value(username))
                .andExpect(jsonPath("$[0].username").value(username));
    }

    @Test
    @Transactional
    public void testResourceMemberAttached() throws Exception {
        mockMvc.perform(post("/resources/add-administrator-to-resource")
                .param("resId", resourceOne.getId().toString())
                .param("username", username))
                .andExpect(status().isCreated());
    }

    @Test
    @Transactional
    public void testResourceMemberAttachNotExisting() throws Exception {
        mockMvc.perform(post("/resources/add-administrator-to-resource")
                .param("resId", resourceOne.getId().toString())
                .param("username", "agdjiojdft"))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void testResourceMemberDetached() throws Exception {
        mockMvc.perform(post("/resources/delete-administrator-from-resource")
                .param("resId", resourceOne.getId().toString())
                .param("adminId", accountId.toString()))
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
