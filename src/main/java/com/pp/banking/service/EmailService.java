package com.pp.banking.service;

import com.pp.banking.dto.EmailDataDto;
import com.pp.banking.dto.mappers.CycleAvoidingMappingContext;
import com.pp.banking.dto.mappers.EmailDataMapper;
import com.pp.banking.dto.mappers.UserMapper;
import com.pp.banking.exception.NotUniqueException;
import com.pp.banking.model.EmailData;
import com.pp.banking.model.User;
import com.pp.banking.repository.EmailRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EmailService {

	private final EmailRepository emailRepository;
	private final UserService userService;
	private final EmailDataMapper emailDataMapper;
	private final UserMapper userMapper;
	private final CacheManager cacheManager;

	@Caching(
		evict = {
			@CacheEvict(
				value = "UserService::findUsers", allEntries = true
			),
			@CacheEvict(
				value = "UserService::getUserById",
				key = "#userId"
			)
		}
	)
	@Transactional
	public EmailDataDto addEmail(EmailDataDto emailDataDto, Long userId) {
		checkEmailUniqueness(emailDataDto);
		EmailData emailData = emailDataMapper.toEntity(
			emailDataDto, new CycleAvoidingMappingContext()
		);
		emailData.setUser(userMapper.toEntity(
			userService.getUserById(userId), new CycleAvoidingMappingContext()
		));
		return emailDataMapper.toDto(
			emailRepository.save(emailData), new CycleAvoidingMappingContext()
		);
	}

	@Transactional
	public EmailDataDto updateEmail(EmailDataDto emailDataDto) {
		checkEmailUniqueness(emailDataDto);
		EmailData emailData = emailDataMapper.toEntity(
			emailDataDto, new CycleAvoidingMappingContext()
		);
		emailData.setUser(getUserByEmailId(emailData.getId()));
		emailData = emailRepository.save(emailData);

		cacheManager.getCache("UserService::findUsers").clear();
		cacheManager.getCache("UserService::getUserById").evict(emailData.getUser().getId());

		return emailDataMapper.toDto(emailData, new CycleAvoidingMappingContext());
	}

	@Transactional
	public void deleteEmail(Long emailId) {
		User user = getUserByEmailId(emailId);
		long emailCount = emailRepository.countByUserId(user.getId());

		if (emailCount <= 1) {
			throw new IllegalStateException("Cannot delete the last email address");
		}

		emailRepository.deleteById(emailId);

		cacheManager.getCache("UserService::findUsers").clear();
		cacheManager.getCache("UserService::getUserById").evict(user.getId());
	}

	@Transactional(readOnly = true)
	public boolean isEmailOwner(Long userId, Long emailId) {
		return emailRepository.isEmailOwner(userId, emailId);
	}

	private void checkEmailUniqueness(EmailDataDto emailDataDto) {
		if (emailExists(emailDataDto.getEmail())) {
			throw new NotUniqueException("Email " + emailDataDto.getEmail() + " already exists");
		}
	}

	private boolean emailExists(String email) {
		return emailRepository.existsByEmail(email);
	}

	public User getUserByEmailId(Long emailId) {
		return emailRepository.findUserByEmailId(emailId)
			.orElseThrow(() -> new EntityNotFoundException("User by email id " + emailId + " not found"));
	}

}
