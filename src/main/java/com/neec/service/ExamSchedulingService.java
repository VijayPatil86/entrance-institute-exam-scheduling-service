package com.neec.service;

import com.neec.dto.CreateExamCenterRequestDTO;

public interface ExamSchedulingService {
	// Admin Operations
	void createExamCenter(CreateExamCenterRequestDTO dto);
	//void addExamSlot(Long centerId, ExamSlotDTO dto);
	//List<ExamCenterDTO> getAllCenters();

	// Applicant Operations
	//List<String> getAvailableCities();
	//List<ExamSlotDTO> findAvailableSlots(String city);
	//void bookSlot(Long userId, Long slotId);
}
