package com.neec.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Builder @AllArgsConstructor @NoArgsConstructor @FieldDefaults(level = AccessLevel.PRIVATE) @Getter
public class ExamSlotResponse {
	Long slotId;
	LocalDate examDate;
	LocalTime examStartTime;
	LocalTime examEndTime;
	int availableSeats;

	Long centerId;
	String centerName;
	String centerAddress;
	String centerCity;
	String centerState;
	String centerPinCode;
}
