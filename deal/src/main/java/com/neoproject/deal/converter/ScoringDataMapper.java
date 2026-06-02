package com.neoproject.deal.converter;

import com.neoproject.deal.model.dto.ScoringDataDto;
import com.neoproject.deal.model.entity.Statement;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ScoringDataMapper {

    @Mapping(source = "statement.appliedOffer.requestAmount", target = "amount")
    @Mapping(source = "statement.appliedOffer.term", target = "term")
    @Mapping(source = "statement.client.firstName", target = "firstName")
    @Mapping(source = "statement.client.lastName", target = "lastName")
    @Mapping(source = "statement.client.middleName", target = "middleName")
    @Mapping(source = "statement.client.gender", target = "gender")
    @Mapping(source = "statement.client.birthdate", target = "birthdate")
    @Mapping(source = "statement.client.passport.series", target = "passportSeries")
    @Mapping(source = "statement.client.passport.number", target = "passportNumber")
    @Mapping(source = "statement.client.passport.issueDate", target = "passportIssueDate")
    @Mapping(source = "statement.client.passport.issueBranch", target = "passportIssueBranch")
    @Mapping(source = "statement.client.maritalStatus", target = "maritalStatus")
    @Mapping(source = "statement.client.dependentAmount", target = "dependentAmount")
    @Mapping(source = "statement.client.employment", target = "employment")
    @Mapping(source = "statement.client.accountNumber", target = "accountNumber")
    @Mapping(source = "statement.appliedOffer.isInsuranceEnabled", target = "isInsuranceEnabled")
    @Mapping(source = "statement.appliedOffer.isSalaryClient", target = "isSalaryClient")
    ScoringDataDto toDto(Statement statement);
}
