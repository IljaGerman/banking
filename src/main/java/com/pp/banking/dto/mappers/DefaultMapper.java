package com.pp.banking.dto.mappers;

import org.mapstruct.Context;

public interface DefaultMapper<D, E> {

	D toDto(E e, @Context CycleAvoidingMappingContext context);

	E toEntity(D d, @Context CycleAvoidingMappingContext context);

}
