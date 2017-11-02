package com.alxgrk.level3;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;
import java.util.Date;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import org.springframework.http.HttpStatus;

import com.alxgrk.level3.error.exceptions.timeslot.NoAvailableTimeslotsException;
import com.alxgrk.level3.error.exceptions.timeslot.TimeslotsNotAvailableException;
import com.alxgrk.level3.error.exceptions.timeslot.TimeslotsToBookClashException;
import com.alxgrk.level3.models.Account;
import com.alxgrk.level3.models.Organization;
import com.alxgrk.level3.models.Resource;
import com.alxgrk.level3.models.Timeslot;
import com.alxgrk.level3.repos.AccountRepository;
import com.alxgrk.level3.repos.OrganizationRepository;
import com.alxgrk.level3.repos.ResourceRepository;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import lombok.extern.slf4j.Slf4j;

@EntityScan(basePackageClasses = { Level3Application.class, Jsr310JpaConverters.class })
@SpringBootApplication
@Slf4j
public class Level3Application {

    public static void main(String[] args) {
        SpringApplication.run(Level3Application.class, args);
    }

    // CORS
    @Bean
    FilterRegistrationBean corsFilter(
            @Value("${tagit.origin:http://localhost:9000}") String origin) {
        return new FilterRegistrationBean(new Filter() {
            public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
                    throws IOException, ServletException {
                HttpServletRequest request = (HttpServletRequest) req;
                HttpServletResponse response = (HttpServletResponse) res;
                String method = request.getMethod();
                // this origin value could just as easily have come from a
                // database
                response.setHeader("Access-Control-Allow-Origin", origin);
                response.setHeader("Access-Control-Allow-Methods", "POST,GET,OPTIONS,DELETE");
                response.setHeader("Access-Control-Max-Age", Long.toString(60 * 60));
                response.setHeader("Access-Control-Allow-Credentials", "true");
                response.setHeader("Access-Control-Allow-Headers",
                        "Origin,Accept,X-Requested-With,Content-Type,Access-Control-Request-Method,Access-Control-Request-Headers,Authorization");
                if ("OPTIONS".equals(method)) {
                    response.setStatus(HttpStatus.OK.value());
                } else {
                    chain.doFilter(req, res);
                }
            }

            public void init(FilterConfig filterConfig) {
            }

            public void destroy() {
            }
        });
    }

    @Profile("!test")
    @Bean
    @Transactional
    CommandLineRunner init(AccountRepository accountRepository,
            OrganizationRepository orgRepository, ResourceRepository resourceRepository) {

        Organization organization = new Organization().setName("Spring");
        if (orgRepository.exists(Example.of(organization))) {
            Organization savedOrg = orgRepository.findOne(Example.of(organization));
            organization.setId(savedOrg.getId());
        }
        orgRepository.save(organization);

        return (evt) -> Arrays.asList("jhoeller,dsyer,pwebb,ogierke,rwinch,mfisher,mpollack,jlong"
                .split(",")).forEach(a -> {
                    Account account = new Account(a, "password")
                            .setSurname(String.valueOf(a.charAt(0)))
                            .setName(a.substring(1))
                            .setDateOfBirth(new Date(1509665674));
                    account.setOrganization(organization);
                    if (accountRepository.exists(Example.of(account))) {
                        Account savedAccount = accountRepository.findOne(Example.of(account));
                        account.setId(savedAccount.getId());
                    }
                    accountRepository.save(account);

                    Timeslot availableTimeslot = new Timeslot(LocalDateTime.now(), LocalDateTime.of(
                            2200, Month.JANUARY, 1, 12, 0));
                    Timeslot bookedTimeslot = new Timeslot(LocalDateTime.of(2100, Month.JANUARY, 1,
                            12, 0), LocalDateTime.of(2101, Month.JANUARY, 1, 12, 0));

                    Resource resource = new Resource();
                    try {
                        resource.setName("garage")
                                .setAdministrators(Sets.newHashSet(account))
                                .setAvailableTimeslots(Lists.newArrayList(availableTimeslot))
                                .setBookedTimeslots(Lists.newArrayList(bookedTimeslot));
                    } catch (NoAvailableTimeslotsException | TimeslotsNotAvailableException
                            | TimeslotsToBookClashException e) {
                        log.error("Could not create resource " + resource + "; " + e);
                    }

                    if (resourceRepository.exists(Example.of(resource))) {
                        Resource savedResource = resourceRepository.findOne(Example.of(resource));
                        resource.setId(savedResource.getId());
                    }
                    resourceRepository.save(resource);

                    account.setConnectedResources(Sets.newHashSet(resource));
                    accountRepository.save(account);
                });
    }

}
