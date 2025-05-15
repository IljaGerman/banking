package com.pp.banking.repository;

import com.pp.banking.model.EmailData;
import com.pp.banking.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmailRepository extends JpaRepository<EmailData, Long> {

	boolean existsByEmail(String email);

	@Query("SELECT e.user FROM EmailData e WHERE e.id = :emailId")
	Optional<User> findUserByEmailId(@Param("emailId") Long emailId);

	@Query("SELECT COUNT(e) FROM EmailData e WHERE e.user.id = :userId")
	long countByUserId(@Param("userId") Long userId);

	@Query(value = """
		 SELECT exists(
		               SELECT 1
		               FROM banking.email_data
		               WHERE user_id = :userId
		                 AND id = :emailId)
		""", nativeQuery = true)
	boolean isEmailOwner(@Param("userId") Long userId, @Param("emailId") Long emailId);

}
