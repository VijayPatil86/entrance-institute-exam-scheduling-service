package com.neec.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.neec.annotation.ValidExamSlotTime;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Builder @AllArgsConstructor @NoArgsConstructor @FieldDefaults(level = AccessLevel.PRIVATE) @Getter @Setter
@ValidExamSlotTime
public class CreateExamSlotRequest {
	@NotNull(message = "Exam Date is required")
	@Future(message = "Exam Date must be in future")
	@JsonFormat(pattern = "yyyy-MM-dd")
	// If the input format is incorrect (e.g., "12/03/2025"), Jackson will automatically fail, and you can handle the error globally.
	LocalDate examDate;

	@NotNull(message = "Exam Start Time is required")
	@JsonFormat(pattern = "hh:mm a")	// hh: 1-12, mm: 00-59, a: AM/PM
	// If the input doesn’t match the format, Jackson will throw a parsing exception automatically (which you can handle globally).
	LocalTime examStartTime;

	@NotNull(message = "Exam End Time is required")
	@JsonFormat(pattern = "hh:mm a")
	LocalTime examEndTime;

	@Digits(fraction = 0, integer = 3, message = "Total Seats must be a numeric")
	@Min(value = 1, message = "Total Seats must be greater than 0")
	@Max(value = 100, message = "Total Seats must be less than or equal to 100")
	int totalSeats;
}
