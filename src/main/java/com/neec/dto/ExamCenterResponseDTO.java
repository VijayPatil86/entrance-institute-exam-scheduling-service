package com.neec.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Builder @AllArgsConstructor @NoArgsConstructor @FieldDefaults(level = AccessLevel.PRIVATE) @Getter
public class ExamCenterResponseDTO {
	Long centerId;
	String centerName;
	String centerAddress;
	String centerCity;
	String centerState;
	String centerPinCode;
	String centerContactPerson;
	String centerContactPhone;
}
