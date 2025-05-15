package com.pp.banking.service;

import com.pp.banking.dto.PageDto;
import com.pp.banking.dto.UserDto;
import com.pp.banking.dto.mappers.CycleAvoidingMappingContext;
import com.pp.banking.dto.mappers.UserMapper;
import com.pp.banking.model.User;
import com.pp.banking.repository.UserRepository;
import com.pp.banking.specification.UserSpecification;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.springframework.data.jpa.domain.Specification.where;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final UserMapper userMapper;

	@Cacheable(
		value = "UserService::getUserById",
		key = "#userId"
	)
	@Transactional(readOnly = true)
	public UserDto getUserById(Long userId) {
		User user = userRepository.findById(userId)
			.orElseThrow(() ->
				new EntityNotFoundException("User with id " + userId + " not found")
			);
		return userMapper.toDto(user, new CycleAvoidingMappingContext());
	}

	@Transactional(readOnly = true)
	public UserDto getUserByEmail(String email) {
		User user = userRepository.findByEmail(email)
			.orElseThrow(() ->
				new EntityNotFoundException("User with email " + email + " not found")
			);
		return userMapper.toDto(user, new CycleAvoidingMappingContext());
	}

	@Transactional(readOnly = true)
	public UserDto getUserByPhone(String phone) {
		User user = userRepository.findByPhone(phone)
			.orElseThrow(
				() -> new EntityNotFoundException("User with phone " + phone + " not found")
			);
		return userMapper.toDto(user, new CycleAvoidingMappingContext());
	}

	@Cacheable(
		value = "UserService::findUsers",
		key = "#dateOfBirth + '.' + #phone + '.' + #name + '.' + #email + #pageable"
	)
	@Transactional(readOnly = true)
	public PageDto<UserDto> findUsers(
		LocalDate dateOfBirth,
		String phone,
		String name,
		String email,
		Pageable pageable
	) {
		return new PageDto<>(userRepository.findAll(
			where(UserSpecification.hasDateOfBirthBefore(dateOfBirth))
				.and(UserSpecification.containsPhone(phone))
				.and(UserSpecification.containsName(name))
				.and(UserSpecification.hasEmail(email)),
			pageable
		).map(u -> userMapper.toDto(u, new CycleAvoidingMappingContext())));
	}

	@Transactional(readOnly = true)
	public boolean existsById(Long id) {
		return userRepository.existsById(id);
	}

}
