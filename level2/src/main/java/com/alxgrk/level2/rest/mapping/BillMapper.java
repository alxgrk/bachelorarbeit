package com.alxgrk.level2.rest.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.alxgrk.level2.models.Bill;
import com.alxgrk.level2.rest.rto.BillRto;

@Mapper(componentModel = "spring")
public interface BillMapper {

    BillMapper INSTANCE = Mappers.getMapper(BillMapper.class);

    BillRto billToBillRto(Bill bill);

}