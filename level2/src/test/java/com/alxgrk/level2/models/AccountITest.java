/*
 * Created on May 10, 2017
 *
 * author age
 */
package com.alxgrk.level2.models;

import javax.transaction.Transactional;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alxgrk.level2.Level2Application;
import com.alxgrk.level2.models.Account;
import com.alxgrk.level2.repos.AccountRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Level2Application.class)
@ActiveProfiles("test")
@Transactional
@DataJpaTest
public class AccountITest {

    private static final String USERNAME = "alxgrk";

    private static final String PASSWORD = "password";

    @Autowired
    private AccountRepository repo;

    @Before
    public void setUp() throws Exception {
        repo.deleteAllInBatch();
    }

    @Test
    public void testSaveOne() throws Exception {
        Account account = new Account(USERNAME, PASSWORD);

        repo.save(account);
    }

    @After
    public void tearDown() throws Exception {
        repo.deleteAllInBatch();
    }
}
