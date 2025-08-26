package com.neec.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;

import com.neec.enums.SlotBookingStatus;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Builder @NoArgsConstructor @AllArgsConstructor @FieldDefaults(level = AccessLevel.PRIVATE) @Getter
public class CreateSlotBookingResponseDTO {
	Long bookingId;
	SlotBookingStatus slotBookingStatus;
	OffsetDateTime slotBookingDateTime;

	// slot details
	Long slotId;
	LocalDate examDate;
	LocalTime examStartTime;
	LocalTime examEndTime;

	// exam center info
	String centerName;
	String centerAddress;
	String centerCity;
}
