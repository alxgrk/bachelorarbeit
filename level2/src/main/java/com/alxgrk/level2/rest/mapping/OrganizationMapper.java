package com.alxgrk.level2.rest.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import com.alxgrk.level2.models.Organization;
import com.alxgrk.level2.rest.rto.OrganizationRto;

@Mapper(componentModel = "spring")
public interface OrganizationMapper {

    OrganizationMapper INSTANCE = Mappers.getMapper(OrganizationMapper.class);

    OrganizationRto organizationToOrganizationRto(Organization organization);

    Organization organizationRtoToOrganization(OrganizationRto organization);

    @Mapping(target = "id", ignore = true)
    void updateOrganizationFromOrganizationRto(OrganizationRto rto,
            @MappingTarget Organization organization);
}