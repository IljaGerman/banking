package com.pp.banking.dto.mappers;

import com.pp.banking.dto.UserDto;
import com.pp.banking.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper extends DefaultMapper<UserDto, User> {
}
