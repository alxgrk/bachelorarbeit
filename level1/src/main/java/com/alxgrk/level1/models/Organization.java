/*
 * Created on May 16, 2017
 *
 * author age
 */
package com.alxgrk.level1.models;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "name"))
@Accessors(chain = true)
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class Organization implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @OneToMany(mappedBy = "organization")
    private Set<Account> members = new HashSet<>();

}
