package com.neec.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.neec.dto.CreateExamCenterRequestDTO;
import com.neec.dto.CreateExamCenterResponseDTO;
import com.neec.dto.CreateExamSlotRequest;
import com.neec.service.ExamSchedulingService;

import jakarta.validation.Valid;

@RestController
@CrossOrigin
@RequestMapping("/api/v1")
public class ExamSchedulingController {
	private ExamSchedulingService examSchedulingService;

	public ExamSchedulingController(ExamSchedulingService examSchedulingService) {
		this.examSchedulingService = examSchedulingService;
	}

	@PostMapping("/admin/centers")
	ResponseEntity<?> createCenter(@Valid @RequestBody CreateExamCenterRequestDTO dto) {
		CreateExamCenterResponseDTO createdExamCenter = examSchedulingService.createExamCenter(dto);
		return ResponseEntity.status(HttpStatus.CREATED.value()).body(createdExamCenter);
	}

	@PostMapping("/admin/centers/{centerId}/slots")
	ResponseEntity<?> addExamSlot(@PathVariable(name = "centerId") Long centerId,
			@Valid @RequestBody CreateExamSlotRequest slotRequest){
		examSchedulingService.addExamSlot(centerId, slotRequest);
		return ResponseEntity.status(HttpStatus.CREATED.value()).body(Map.of("status", "Exam Slot added successfully"));
	}
}
