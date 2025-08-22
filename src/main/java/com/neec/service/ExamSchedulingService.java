package com.neec.service;

import com.neec.dto.CreateExamCenterRequestDTO;
import com.neec.dto.CreateExamCenterResponseDTO;
import com.neec.dto.CreateExamSlotRequest;

public interface ExamSchedulingService {
	// Admin Operations
	CreateExamCenterResponseDTO createExamCenter(CreateExamCenterRequestDTO dto);
	void addExamSlot(Long centerId, CreateExamSlotRequest dto);
	//List<ExamCenterDTO> getAllCenters();

	// Applicant Operations
	//List<String> getAvailableCities();
	//List<ExamSlotDTO> findAvailableSlots(String city);
	//void bookSlot(Long userId, Long slotId);
}
