package com.neec.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.neec.dto.CreateExamCenterRequestDTO;
import com.neec.dto.CreateExamCenterResponseDTO;
import com.neec.dto.CreateExamSlotRequest;
import com.neec.entity.ExamCenter;
import com.neec.entity.ExamSlot;
import com.neec.repository.ExamCenterRepository;
import com.neec.repository.ExamSlotRepository;

import io.micrometer.observation.annotation.Observed;
import jakarta.persistence.EntityManager;

@Service
@Transactional
public class ExamSchedulingServiceImpl implements ExamSchedulingService {
	private ExamCenterRepository examCenterRepository;
	private ExamSlotRepository examSlotRepository;
	private EntityManager entityManager;

	public ExamSchedulingServiceImpl(ExamCenterRepository examCenterRepository,
			ExamSlotRepository examSlotRepository, EntityManager entityManager) {
		this.examCenterRepository = examCenterRepository;
		this.examSlotRepository = examSlotRepository;
		this.entityManager = entityManager;
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

	@Observed(name = "scheduling.service.add.slot", contextualName = "adding new exam slot")
	@Transactional
	@Override
	public void addExamSlot(Long centerId, CreateExamSlotRequest dto) {
		if(!examCenterRepository.existsById(centerId))
			throw new IllegalArgumentException("Exam Center with id " + centerId + " not found.");
		if(examSlotRepository.existsByExamCenter_CenterIdAndExamDateAndStartTimeAndEndTime(
				centerId, dto.getExamDate(), dto.getExamStartTime(), dto.getExamEndTime())
		) {
			String exceptionMessage = String.format(
					"Exam Slot with centerId=%d, date=%s, start time=%s, end time=%s already exists.",
					centerId, dto.getExamDate(), dto.getExamStartTime(), dto.getExamEndTime()
			);
			throw new IllegalArgumentException(exceptionMessage);
		}
		ExamCenter examCenter = entityManager.getReference(ExamCenter.class, centerId);
		ExamSlot examSlot = ExamSlot.builder()
				.examCenter(examCenter)
				.examDate(dto.getExamDate())
				.startTime(dto.getExamStartTime())
				.endTime(dto.getExamEndTime())
				.totalSeats(dto.getTotalSeats())
				.build();
		examSlotRepository.save(examSlot);
	}
}
