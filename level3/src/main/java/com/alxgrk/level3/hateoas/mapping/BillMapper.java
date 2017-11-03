package com.alxgrk.level3.hateoas.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.alxgrk.level3.hateoas.rto.BillRto;
import com.alxgrk.level3.models.Bill;

@Mapper(componentModel = "spring")
public interface BillMapper {

    BillMapper INSTANCE = Mappers.getMapper(BillMapper.class);

    BillRto billToBillRto(Bill bill);

}