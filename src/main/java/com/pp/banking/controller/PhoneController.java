package com.pp.banking.controller;

import com.pp.banking.dto.PhoneDataDto;
import com.pp.banking.dto.validation.OnUpdate;
import com.pp.banking.service.PhoneService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/phones")
@RequiredArgsConstructor
public class PhoneController {

	private final PhoneService phoneService;

	@Operation(summary = "Update user phone")
	@PutMapping
	@PreAuthorize("@cse.canAccessPhone(#phoneData.id)")
	public PhoneDataDto update(@Validated(OnUpdate.class) @RequestBody PhoneDataDto phoneData) {
		return phoneService.updatePhone(phoneData);
	}

	@Operation(summary = "Remove user phone")
	@DeleteMapping("/{id}")
	@PreAuthorize("@cse.canAccessPhone(#id)")
	public void deleteById(@PathVariable final Long id) {
		phoneService.deletePhone(id);
	}

}
