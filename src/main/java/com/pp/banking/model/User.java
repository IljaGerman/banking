package com.pp.banking.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Table(name = "user", schema = "banking")
@Entity
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;
	private LocalDate dateOfBirth;
	private String password;

	@OneToOne(mappedBy = "user")
	private Account account;

	@OneToMany(mappedBy = "user")
	private Set<EmailData> emails = new HashSet<>();

	@OneToMany(mappedBy = "user")
	private Set<PhoneData> phones = new HashSet<>();

}
