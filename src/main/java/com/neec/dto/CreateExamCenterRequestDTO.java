package com.neec.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor @NoArgsConstructor @FieldDefaults(level = AccessLevel.PRIVATE) @Setter @Getter
public class CreateExamCenterRequestDTO {
	@NotBlank(message = "Exam Center Name is required")
	String centerName;

	@NotBlank(message = "Exam Center Address is required")
	String centerAddress;

	@NotBlank(message = "Exam Center City is required")
	String centerCity;

	@NotBlank(message = "Exam Center State is required")
	String centerState;

	@NotBlank(message = "Exam Center Pin Code is required")
	@Size(min = 6, max = 6, message = "Exam Center Pin Code must have exactly 6 digits")
	@Digits(fraction = 0, integer = 6, message = "Exam Center Pin Code must have exactly 6 digits")
	@Pattern(regexp = "^[0-9]{6}$", message = "Exam Center Pin Code must have exactly 6 digits")
	String centerPinCode;

	@NotBlank(message = "Exam Center Contact Person Name is required")
	String centerContactPerson;

	@NotBlank(message = "Exam Center Contact Phone Number is required")
	@Size(min = 10, max = 10, message = "Exam Center Contact Phone Number must have exactly 10 digits")
	@Digits(fraction = 0, integer = 10, message = "Exam Center Contact Phone Number must have exactly 10 digits")
	@Pattern(regexp = "^[7-9][0-9]{9}$", message = "Exam Center Contact Phone Number must be 10 digits starting with 7, 8, or 9")
	String centerContactPhone;
}
