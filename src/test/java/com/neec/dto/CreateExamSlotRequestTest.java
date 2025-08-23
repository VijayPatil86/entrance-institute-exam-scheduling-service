package com.neec.dto;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

public class CreateExamSlotRequestTest {
	private Validator validator;

	@BeforeEach
	void init() {
		this.validator = Validation.buildDefaultValidatorFactory().getValidator();
	}

	// Exam Date
	@Test
	void test_examDate_Null() {
		LocalDate examDate = null;
		CreateExamSlotRequest slotRequest = CreateExamSlotRequest.builder()
				.examDate(examDate)
				.build();
		Set<ConstraintViolation<CreateExamSlotRequest>> violations =
				validator.validateProperty(slotRequest, "examDate");
		assertFalse(violations.isEmpty());
	}

	@Test
	void test_examDate_isPastDate_IsInvalid() {
		CreateExamSlotRequest slotRequest = CreateExamSlotRequest.builder()
				.examDate(LocalDate.of(2022, 01, 01))
				.build();
		Set<ConstraintViolation<CreateExamSlotRequest>> violations =
				validator.validateProperty(slotRequest, "examDate");
		assertFalse(violations.isEmpty());
	}

	@Test
	void test_examDate_isToday_IsInvalid() {
		CreateExamSlotRequest slotRequest = CreateExamSlotRequest.builder()
				.examDate(LocalDate.now())
				.build();
		Set<ConstraintViolation<CreateExamSlotRequest>> violations =
				validator.validateProperty(slotRequest, "examDate");
		assertFalse(violations.isEmpty());
	}

	@Test
	void test_examDate_isFutureDate_IsValid() {
		CreateExamSlotRequest slotRequest = CreateExamSlotRequest.builder()
				.examDate(LocalDate.now().plus(1, ChronoUnit.DAYS))
				.build();
		Set<ConstraintViolation<CreateExamSlotRequest>> violations =
				validator.validateProperty(slotRequest, "examDate");
		assertTrue(violations.isEmpty());
	}

	// Exam Start Time
	@Test
	void test_examStartTime_Null() {
		LocalTime startTime = null;
		CreateExamSlotRequest slotRequest = CreateExamSlotRequest.builder()
				.examStartTime(startTime)
				.build();
		Set<ConstraintViolation<CreateExamSlotRequest>> violations =
				validator.validateProperty(slotRequest, "examStartTime");
		assertFalse(violations.isEmpty());
	}

	@ParameterizedTest
	@ValueSource(strings = {"11:25"})
	void test_examStartTime_Valid(String input) {
		CreateExamSlotRequest slotRequest = CreateExamSlotRequest.builder()
				.examStartTime(LocalTime.parse(input))
				.build();
		Set<ConstraintViolation<CreateExamSlotRequest>> violations =
				validator.validateProperty(slotRequest, "examStartTime");
		assertTrue(violations.isEmpty());
	}

	// Exam End Time - skipped as similar to Exam Start Time

	// Exam Date (Future) + Exam Start Time > Exam End Time, 25 Dec 2027 10:00 - 09:00
	@Test
	void test_startTimeIsAfterEndTime_IsInvalid() {
		CreateExamSlotRequest slotRequest = CreateExamSlotRequest.builder()
				.examDate(LocalDate.now().plus(1, ChronoUnit.DAYS))
				.examStartTime(LocalTime.of(11, 0))
				.examEndTime(LocalTime.of(10, 0))
				.build();
		Set<ConstraintViolation<CreateExamSlotRequest>> violations =
				validator.validate(slotRequest);
		assertFalse(violations.isEmpty());
	}

	@Test
	void test_allFieldsAreValid() {
		CreateExamSlotRequest slotRequest = CreateExamSlotRequest.builder()
				.examDate(LocalDate.now().plus(1, ChronoUnit.DAYS))
				.examStartTime(LocalTime.of(10, 00))
				.examEndTime(LocalTime.of(12, 00))
				.totalSeats(10)
				.build();
		Set<ConstraintViolation<CreateExamSlotRequest>> violations =
				validator.validate(slotRequest);
		assertTrue(violations.isEmpty());
	}
}
