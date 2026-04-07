package com.neoproject.deal.converter;

import com.neoproject.deal.model.dto.FinishRegistrationRequestDto;
import com.neoproject.deal.model.entity.Statement;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface StatementMapper {

    @Mapping(source = "dto.gender", target = "client.gender")
    @Mapping(source = "dto.maritalStatus", target = "client.maritalStatus")
    @Mapping(source = "dto.dependentAmount", target = "client.dependentAmount")
    @Mapping(source = "dto.passportIssueDate", target = "client.passport.issueDate")
    @Mapping(source = "dto.passportIssueBranch", target = "client.passport.issueBranch")
    @Mapping(source = "dto.employment", target = "client.employment")
    @Mapping(source = "dto.accountNumber", target = "client.accountNumber")
    void updateStatementFromDto(@MappingTarget Statement statement, FinishRegistrationRequestDto dto);
}
