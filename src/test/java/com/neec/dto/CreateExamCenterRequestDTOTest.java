package com.neec.dto;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

public class CreateExamCenterRequestDTOTest {
	private Validator validator;

	@BeforeEach
	void init() {
		this.validator = Validation.buildDefaultValidatorFactory().getValidator();
	}

	@ParameterizedTest
	@NullSource
	@ValueSource(strings = {"", "   "})
	void test_centerName_Invalid(String input) {
		CreateExamCenterRequestDTO dto = new CreateExamCenterRequestDTO();
		dto.setCenterName(input);
		Set<ConstraintViolation<CreateExamCenterRequestDTO>> violations =
				validator.validateProperty(dto, "centerName");
		assertFalse(violations.isEmpty());
	}

	@Test
	void test_centerName_Valid() {
		CreateExamCenterRequestDTO dto = new CreateExamCenterRequestDTO();
		dto.setCenterName("A B College");
		Set<ConstraintViolation<CreateExamCenterRequestDTO>> violations =
				validator.validateProperty(dto, "centerName");
		assertTrue(violations.isEmpty());
	}

	@ParameterizedTest
	@NullSource
	@ValueSource(strings = {"", " ", "abcdef", "12345", "1234567", "12@345"})
	void test_centerPinCode_Invalid(String input) {
		CreateExamCenterRequestDTO dto = new CreateExamCenterRequestDTO();
		dto.setCenterPinCode(input);
		Set<ConstraintViolation<CreateExamCenterRequestDTO>> violations =
				validator.validateProperty(dto, "centerPinCode");
		assertFalse(violations.isEmpty());
	}

	@Test
	void test_centerPinCode_Valid() {
		CreateExamCenterRequestDTO dto = new CreateExamCenterRequestDTO();
		dto.setCenterPinCode("123456");
		Set<ConstraintViolation<CreateExamCenterRequestDTO>> violations =
				validator.validateProperty(dto, "centerPinCode");
		assertTrue(violations.isEmpty());
	}
}
