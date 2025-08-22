package com.neec.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.neec.dto.CreateExamCenterRequestDTO;
import com.neec.dto.CreateExamCenterResponseDTO;
import com.neec.dto.CreateExamSlotRequest;
import com.neec.entity.ExamCenter;
import com.neec.repository.ExamCenterRepository;

@Service
@Transactional
public class ExamSchedulingServiceImpl implements ExamSchedulingService {
	private ExamCenterRepository examCenterRepository;

	public ExamSchedulingServiceImpl(ExamCenterRepository examCenterRepository) {
		this.examCenterRepository = examCenterRepository;
	}

	@Transactional
	@Override
	public CreateExamCenterResponseDTO createExamCenter(CreateExamCenterRequestDTO dto) {
		if(examCenterRepository.existsByCenterName(dto.getCenterName())) {
			throw new IllegalArgumentException("An Exam Center with this Name already exists.");
		}
		ExamCenter examCenter = ExamCenter.builder()
				.centerName(dto.getCenterName())
				.addressLine(dto.getCenterAddress())
				.city(dto.getCenterCity())
				.state(dto.getCenterState())
				.pinCode(dto.getCenterPinCode())
				.contactPerson(dto.getCenterContactPerson())
				.contactPhone(dto.getCenterContactPhone())
				.build();
		ExamCenter savedExamCenter = examCenterRepository.save(examCenter);
		return CreateExamCenterResponseDTO.builder()
				.centerId(savedExamCenter.getCenterId())
				.centerName(savedExamCenter.getCenterName())
				.centerAddress(savedExamCenter.getAddressLine())
				.centerCity(savedExamCenter.getCity())
				.centerState(savedExamCenter.getState())
				.centerPinCode(savedExamCenter.getPinCode())
				.centerContactPerson(savedExamCenter.getContactPerson())
				.centerContactPhone(savedExamCenter.getContactPhone())
				.build();
	}

	@Transactional
	@Override
	public void addExamSlot(Long centerId, CreateExamSlotRequest dto) {
		
	}
}
