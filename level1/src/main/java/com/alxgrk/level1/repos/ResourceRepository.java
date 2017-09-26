/*
 * Created on May 16, 2017
 *
 * author age
 */
package com.alxgrk.level1.repos;

import java.util.Collection;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alxgrk.level1.models.Resource;

public interface ResourceRepository extends JpaRepository<Resource, Long> {

    Optional<Resource> findByName(String name);

    Collection<Resource> findByAdministratorsId(Long id);

}
