package com.pp.banking.specification;

import com.pp.banking.model.EmailData;
import com.pp.banking.model.PhoneData;
import com.pp.banking.model.User;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class UserSpecification {

	public static Specification<User> hasDateOfBirthBefore(LocalDate dateOfBirth) {
		return (root, query, cb) ->
			dateOfBirth == null ? cb.conjunction() : cb.lessThan(root.get("dateOfBirth"), dateOfBirth);
	}

	public static Specification<User> containsPhone(String phone) {
		return (root, query, cb) -> {
			if (phone == null) return cb.conjunction();
			Join<User, PhoneData> phoneJoin = root.join("phones");
			return cb.equal(phoneJoin.get("phone"), phone);
		};
	}

	public static Specification<User> containsName(String name) {
		return (root, query, cb) ->
			name == null ? cb.conjunction() : cb.like(root.get("name"), "%" + name + "%");
	}

	public static Specification<User> hasEmail(String email) {
		return (root, query, cb) -> {
			if (email == null) return cb.conjunction();
			Join<User, EmailData> emailJoin = root.join("emails");
			return cb.equal(emailJoin.get("email"), email);
		};
	}

}
