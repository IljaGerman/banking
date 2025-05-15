package com.pp.banking.dto.mappers;

import com.pp.banking.dto.EmailDataDto;
import com.pp.banking.model.EmailData;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EmailDataMapper extends DefaultMapper<EmailDataDto, EmailData> {
}
