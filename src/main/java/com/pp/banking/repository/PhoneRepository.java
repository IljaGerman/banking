package com.pp.banking.repository;

import com.pp.banking.model.PhoneData;
import com.pp.banking.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PhoneRepository extends JpaRepository<PhoneData, Long> {

	boolean existsByPhone(String phoneNumber);

	@Query("SELECT p.user FROM PhoneData p WHERE p.id = :phoneId")
	Optional<User> findUserByPhoneId(@Param("phoneId") Long phoneId);

	@Query("SELECT COUNT(p) FROM PhoneData p WHERE p.user.id = :userId")
	long countByUserId(@Param("userId") Long userId);

	@Query(value = """
		 SELECT exists(
		               SELECT 1
		               FROM banking.phone_data
		               WHERE user_id = :userId
		                 AND id = :phoneId)
		""", nativeQuery = true)
	boolean isPhoneOwner(@Param("userId") Long userId, @Param("phoneId") Long phoneId);

}
