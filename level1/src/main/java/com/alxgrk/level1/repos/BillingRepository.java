/*
 * Created on May 16, 2017
 *
 * author age
 */
package com.alxgrk.level1.repos;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alxgrk.level1.models.Bill;

public interface BillingRepository extends JpaRepository<Bill, Long> {

}