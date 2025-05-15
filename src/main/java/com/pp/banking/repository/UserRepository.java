package com.pp.banking.repository;

import com.pp.banking.model.User;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

	@Query(value = "SELECT DISTINCT u FROM User u JOIN FETCH u.phones p WHERE p.phone = :phone")
	Optional<User> findByPhone(@Param("phone") String phone);

	@Query(value = "SELECT DISTINCT u FROM User u JOIN u.emails e WHERE e.email = :email")
	Optional<User> findByEmail(@Param("email") String email);

	boolean existsById(@NonNull Long id);

}
