package com.alxgrk.level1.rest.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import com.alxgrk.level1.models.Account;
import com.alxgrk.level1.rest.rto.AccountRto;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    AccountMapper INSTANCE = Mappers.getMapper(AccountMapper.class);

    AccountRto accountToAccountRto(Account account);

    // use update instead of direct conversion, because it's not possible to
    // choose constructor
    @Mapping(target = "id", ignore = true)
    void updateAccountFromAccountRto(AccountRto rto, @MappingTarget Account account);
}