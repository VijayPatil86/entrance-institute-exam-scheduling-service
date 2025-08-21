package com.neec.service;

import com.neec.dto.CreateExamCenterRequestDTO;
import com.neec.dto.CreateExamCenterResponseDTO;

public interface ExamSchedulingService {
	// Admin Operations
	CreateExamCenterResponseDTO createExamCenter(CreateExamCenterRequestDTO dto);
	//void addExamSlot(Long centerId, ExamSlotDTO dto);
	//List<ExamCenterDTO> getAllCenters();

	// Applicant Operations
	//List<String> getAvailableCities();
	//List<ExamSlotDTO> findAvailableSlots(String city);
	//void bookSlot(Long userId, Long slotId);
}
