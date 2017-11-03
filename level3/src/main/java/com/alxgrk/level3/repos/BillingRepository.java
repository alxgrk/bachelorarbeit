/*
 * Created on May 16, 2017
 *
 * author age
 */
package com.alxgrk.level3.repos;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alxgrk.level3.models.Bill;

public interface BillingRepository extends JpaRepository<Bill, Long> {

}
