package com.neec.annotation.impl;

import com.neec.annotation.ValidExamSlotTime;
import com.neec.dto.CreateExamSlotRequest;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ExamTimeValidator implements ConstraintValidator<ValidExamSlotTime, CreateExamSlotRequest> {
	@Override
	public boolean isValid(CreateExamSlotRequest request, ConstraintValidatorContext context) {
		if(request.getExamStartTime() == null || request.getExamEndTime() == null) {
			// Let @NotNull handle null checks separately
			return true;
		}
		return request.getExamStartTime().isBefore(request.getExamEndTime());
	}
}
