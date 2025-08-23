package com.neec.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler(exception = {HttpMessageNotReadableException.class})
	public ResponseEntity<Map<String, String>> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex){
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
}
