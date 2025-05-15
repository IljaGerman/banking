package com.pp.banking.dto.mappers;

import com.pp.banking.dto.PhoneDataDto;
import com.pp.banking.model.PhoneData;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PhoneDataMapper extends DefaultMapper<PhoneDataDto, PhoneData> {
}
