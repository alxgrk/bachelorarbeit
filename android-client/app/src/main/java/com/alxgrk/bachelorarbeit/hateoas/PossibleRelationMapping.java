package com.alxgrk.bachelorarbeit.hateoas;

import com.google.common.collect.Lists;

import org.springframework.http.MediaType;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;

import static com.alxgrk.bachelorarbeit.hateoas.PossibleRelation.ACCOUNTS;
import static com.alxgrk.bachelorarbeit.hateoas.PossibleRelation.ADMINISTRATORS;
import static com.alxgrk.bachelorarbeit.hateoas.PossibleRelation.ATTACH;
import static com.alxgrk.bachelorarbeit.hateoas.PossibleRelation.DETACH;
import static com.alxgrk.bachelorarbeit.hateoas.PossibleRelation.MEMBERS;
import static com.alxgrk.bachelorarbeit.hateoas.PossibleRelation.ORGANIZATION;
import static com.alxgrk.bachelorarbeit.hateoas.PossibleRelation.ORGANIZATIONS;
import static com.alxgrk.bachelorarbeit.hateoas.PossibleRelation.RESOURCES;
import static com.alxgrk.bachelorarbeit.hateoas.PossibleRelation.SELF;

public class PossibleRelationMapping {

    private static final PossibleRelationMapping INSTANCE = new PossibleRelationMapping();

    @Getter
    private final Map<MediaType, List<PossibleRelation>> relsPerMediaTypeMap;

    private PossibleRelationMapping() {
        List<PossibleRelation> rootRelations = Lists.newArrayList(SELF);
        List<PossibleRelation> resourcesRelations = Lists.newArrayList(SELF, RESOURCES);
        List<PossibleRelation> orgsRelations = Lists.newArrayList(SELF, ORGANIZATIONS, ORGANIZATION);
        List<PossibleRelation> accountsRelations = Lists.newArrayList(SELF, ACCOUNTS, ADMINISTRATORS, MEMBERS);
        List<PossibleRelation> genericRelations = Lists.newArrayList(DETACH, ATTACH);

        Map<MediaType, List<PossibleRelation>> relsPerMediaTypeMap = new HashMap<>();
        relsPerMediaTypeMap.put(HateoasMediaType.ROOT_TYPE, rootRelations);
        relsPerMediaTypeMap.put(HateoasMediaType.ACCOUNT_TYPE, accountsRelations);
        relsPerMediaTypeMap.put(HateoasMediaType.RESOURCE_TYPE, resourcesRelations);
        relsPerMediaTypeMap.put(HateoasMediaType.ORGANIZATION_TYPE, orgsRelations);
        relsPerMediaTypeMap.put(MediaType.APPLICATION_JSON, genericRelations);
        
        this.relsPerMediaTypeMap = Collections.unmodifiableMap(relsPerMediaTypeMap);
    }

}
