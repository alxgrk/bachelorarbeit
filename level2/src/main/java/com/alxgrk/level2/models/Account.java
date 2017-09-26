package com.alxgrk.level2.models;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Entity
@Accessors(chain = true)
@Getter
@Setter
@ToString(exclude = { "organization", "connectedResources", "password" })
@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Account {

    @Id
    @GeneratedValue
    private Long id;

    @NonNull
    @Column(nullable = false, unique = true)
    private String username;

    @NonNull
    @Column(nullable = false)
    private String password;

    @NonNull
    @Column(nullable = false)
    private Boolean enabled = true;

    private String surname;

    private String name;

    @ManyToOne(optional = true)
    private Organization organization;

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Resource> connectedResources = new HashSet<>();

}
