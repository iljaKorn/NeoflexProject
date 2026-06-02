package com.neoproject.deal.converter;

import com.neoproject.deal.model.dto.CreditDto;
import com.neoproject.deal.model.entity.Credit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CreditMapper {

    @Mapping(source = "isInsuranceEnabled", target = "insuranceEnabled")
    @Mapping(source = "isSalaryClient", target = "salaryClient")
    @Mapping(target = "creditStatus", ignore = true)
    Credit toEntity(CreditDto dto);
}
