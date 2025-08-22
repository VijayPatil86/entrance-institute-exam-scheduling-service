package com.neec.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.neec.annotation.impl.ExamTimeValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Retention(RUNTIME)
@Target(TYPE)
@Constraint(validatedBy = ExamTimeValidator.class)
public @interface ValidExamSlotTime {
	String message() default "Exam Start Time must be before Exam End Time";
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};
}
