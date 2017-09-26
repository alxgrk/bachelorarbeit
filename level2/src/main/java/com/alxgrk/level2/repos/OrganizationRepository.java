/*
 * Created on May 16, 2017
 *
 * author age
 */
package com.alxgrk.level2.repos;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alxgrk.level2.models.Organization;

public interface OrganizationRepository extends JpaRepository<Organization, Long> {

    Optional<Organization> findByName(String name);

}
