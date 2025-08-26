package com.neec.service;

import java.util.List;

import com.neec.dto.CreateExamCenterRequestDTO;
import com.neec.dto.CreateExamCenterResponseDTO;
import com.neec.dto.CreateExamSlotRequest;
import com.neec.dto.ExamCenterResponseDTO;
import com.neec.dto.ExamSlotResponse;
import com.neec.dto.CreateSlotBookingResponseDTO;

public interface ExamSchedulingService {
	// Admin Operations
	CreateExamCenterResponseDTO createExamCenter(CreateExamCenterRequestDTO dto);
	void addExamSlot(Long centerId, CreateExamSlotRequest dto);
	List<ExamCenterResponseDTO> getAllCenters();

	// Applicant Operations
	List<String> getAvailableCities();
	List<ExamSlotResponse> findAvailableSlots(String city);
	CreateSlotBookingResponseDTO bookSlot(Long userId, Long slotId);
}
