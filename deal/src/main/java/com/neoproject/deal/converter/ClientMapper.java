package com.neoproject.deal.converter;

import com.neoproject.deal.model.dto.LoanStatementRequestDto;
import com.neoproject.deal.model.entity.Client;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ClientMapper {

    @Mapping(source = "passportNumber", target = "passport.number")
    @Mapping(source = "passportSeries", target = "passport.series")
    Client toEntity(LoanStatementRequestDto dto);
}
