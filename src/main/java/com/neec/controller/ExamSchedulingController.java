package com.neec.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.neec.dto.CreateExamCenterRequestDTO;
import com.neec.dto.CreateExamCenterResponseDTO;
import com.neec.dto.CreateExamSlotRequest;
import com.neec.dto.CreateSlotBookingRequestDTO;
import com.neec.dto.CreateSlotBookingResponseDTO;
import com.neec.dto.CustomPrincipal;
import com.neec.service.ExamSchedulingService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

@RestController
@CrossOrigin
@RequestMapping("/api/v1")
@Validated
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

	@GetMapping("/admin/centers")
	ResponseEntity<?> getAllExamCenters(){
		return ResponseEntity.ok(examSchedulingService.getAllCenters());
	}

	@GetMapping("/centers/cities")
	ResponseEntity<List<String>> getAvailableCities(){
		return ResponseEntity.ok(examSchedulingService.getAvailableCities());
	}

	@GetMapping("/centers/slots")
	ResponseEntity<?> getAvailableSlotsByCity(
			@RequestParam(name = "city") @NotBlank(message = "value of request parameter city can not be blank") String city
	){
		return ResponseEntity.ok(examSchedulingService.findAvailableSlots(city));
	}

	@PostMapping("/bookings")
	ResponseEntity<?> bookExamSlot(@Valid @RequestBody CreateSlotBookingRequestDTO createSlotBookingRequestDTO,
			@AuthenticationPrincipal CustomPrincipal customPrincipal){
		Long userId = Long.parseLong(customPrincipal.getSubject());
		Long slotId = createSlotBookingRequestDTO.getSlotId();
		CreateSlotBookingResponseDTO bookingConfirmation =
				examSchedulingService.bookSlot(userId, slotId);
		return ResponseEntity.status(HttpStatus.CREATED).body(bookingConfirmation);
	}
}
