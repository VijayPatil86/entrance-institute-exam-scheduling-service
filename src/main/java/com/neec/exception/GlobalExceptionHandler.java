package com.neec.exception;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler(exception = {HttpMessageNotReadableException.class})
	public ResponseEntity<Map<String, String>> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex){
		if(ex.getCause().getMessage().contains("Unexpected character") ||
		   ex.getCause().getMessage().contains("Unrecognized token")) {
			return ResponseEntity.badRequest().body(Map.of("error", "Malformed JSON: A value is missing or improperly formatted."));
		}
		if(ex.getCause().getMessage().contains("Cannot deserialize value of type `java.lang.Long`")) {
			return ResponseEntity.badRequest().body(Map.of("error", "Invalid value for 'slotId'. Expected a number."));
		}
		return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
	}

	@ExceptionHandler(exception = {IllegalArgumentException.class})
	public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException ex){
		return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
	}

	@ExceptionHandler(exception = {MethodArgumentNotValidException.class})
	public ResponseEntity<Map<String, String>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex){
		Map<String, String> errors = new HashMap<>();
		ex.getFieldErrors().forEach(error ->
			errors.put(error.getField(), error.getDefaultMessage())
		);
		// class level errors like @ValidExamSlotTime
		ex.getGlobalErrors().forEach(error ->
			errors.put(error.getObjectName(), error.getDefaultMessage())
		);
		return ResponseEntity.badRequest().body(errors);
	}

	@ExceptionHandler(exception = {MissingServletRequestParameterException.class})
	public ResponseEntity<?> handleMissingServletRequestParameterException(MissingServletRequestParameterException ex) {
		String message = "The required request parameter '" + ex.getParameterName() + "' is missing";
		return ResponseEntity.badRequest().body(Map.of("error", message));
	}

	@ExceptionHandler(exception = {ConstraintViolationException.class})
	public ResponseEntity<?> handleConstraintViolationException(ConstraintViolationException ex) {
		List<String> errors = ex.getConstraintViolations()
		        .stream()
		        .map(violation -> violation.getMessage())
		        .collect(Collectors.toList());
	    return ResponseEntity.badRequest().body(Map.of("errors", errors));
	}

	@ExceptionHandler(exception = {IllegalStateException.class})
	public ResponseEntity<Map<String, String>> handleIllegalStateException(IllegalStateException ex){
		return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
	}

	@ExceptionHandler(exception = {DataIntegrityViolationException.class})
	public ResponseEntity<Map<String, String>> handleDataIntegrityViolationException(DataIntegrityViolationException ex){
		if(ex.getCause().getMessage().contains("duplicate key value violates unique constraint \"uk_slot_id_user_id\"")) {
			return ResponseEntity.badRequest().body(Map.of("error", "You have already booked this exam slot."));
		}
		return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
	}
}
