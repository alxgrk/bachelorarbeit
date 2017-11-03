package com.alxgrk.level1.rest.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.alxgrk.level1.models.Bill;
import com.alxgrk.level1.rest.rto.BillRto;

@Mapper(componentModel = "spring")
public interface BillMapper {

    BillMapper INSTANCE = Mappers.getMapper(BillMapper.class);

    BillRto billToBillRto(Bill bill);

}