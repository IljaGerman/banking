package com.pp.banking.service;

import com.pp.banking.dto.PhoneDataDto;
import com.pp.banking.dto.mappers.CycleAvoidingMappingContext;
import com.pp.banking.dto.mappers.PhoneDataMapper;
import com.pp.banking.dto.mappers.UserMapper;
import com.pp.banking.model.PhoneData;
import com.pp.banking.model.User;
import com.pp.banking.repository.PhoneRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PhoneService {

	private final PhoneRepository phoneRepository;
	private final UserService userService;
	private final PhoneDataMapper phoneDataMapper;
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
	public PhoneDataDto addPhone(PhoneDataDto phoneDataDto, Long userId) {
		checkPhoneUniqueness(phoneDataDto);
		PhoneData phoneData = phoneDataMapper.toEntity(
			phoneDataDto, new CycleAvoidingMappingContext()
		);
		phoneData.setUser(userMapper.toEntity(
				userService.getUserById(userId), new CycleAvoidingMappingContext()
			)
		);
		return phoneDataMapper.toDto(
			phoneRepository.save(phoneData), new CycleAvoidingMappingContext()
		);
	}

	@Transactional
	public PhoneDataDto updatePhone(PhoneDataDto phoneDataDto) {
		checkPhoneUniqueness(phoneDataDto);
		PhoneData phoneData = phoneDataMapper.toEntity(
			phoneDataDto, new CycleAvoidingMappingContext()
		);
		phoneData.setUser(getUserByPhoneId(phoneData.getId()));
		phoneData = phoneRepository.save(phoneData);

		cacheManager.getCache("UserService::findUsers").clear();
		cacheManager.getCache("UserService::getUserById").evict(phoneData.getUser().getId());

		return phoneDataMapper.toDto(phoneData, new CycleAvoidingMappingContext());
	}

	@Transactional
	public void deletePhone(Long phoneId) {
		User user = getUserByPhoneId(phoneId);
		long phoneCount = phoneRepository.countByUserId(user.getId());

		if (phoneCount <= 1) {
			throw new IllegalStateException("Cannot delete the last phone number");
		}

		phoneRepository.deleteById(phoneId);

		cacheManager.getCache("UserService::findUsers").clear();
		cacheManager.getCache("UserService::getUserById").evict(user.getId());
	}

	private void checkPhoneUniqueness(PhoneDataDto phoneData) {
		if (phoneExists(phoneData.getPhone())) {
			throw new RuntimeException("Phone " + phoneData.getPhone() + " already exists");
		}
	}

	private boolean phoneExists(String phoneNumber) {
		return phoneRepository.existsByPhone(phoneNumber);
	}

	private User getUserByPhoneId(Long phoneId) {
		return phoneRepository.findUserByPhoneId(phoneId)
			.orElseThrow(() -> new EntityNotFoundException("User by phone id " + phoneId + " not found"));
	}

	@Transactional(readOnly = true)
	public boolean isPhoneOwner(Long userId, Long phoneId) {
		return phoneRepository.isPhoneOwner(userId, phoneId);
	}

}
