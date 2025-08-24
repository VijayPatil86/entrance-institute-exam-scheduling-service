package com.neec.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.neec.dto.CreateExamCenterRequestDTO;
import com.neec.dto.CreateExamCenterResponseDTO;
import com.neec.dto.CreateExamSlotRequest;
import com.neec.entity.ExamCenter;
import com.neec.entity.ExamSlot;
import com.neec.repository.ExamCenterRepository;
import com.neec.repository.ExamSlotRepository;

import jakarta.persistence.EntityManager;

@ExtendWith(MockitoExtension.class)
public class ExamSchedulingServiceTest {
	@Mock
	private ExamCenterRepository mockExamCenterRepository;
	@Mock
	private ExamSlotRepository mockExamSlotRepository;
	@Mock
	private EntityManager mockEntityManager;
	@InjectMocks
	private ExamSchedulingServiceImpl examSchedulingServiceImpl;

	@Test
	void test_createExamCenter_NameAlreadyExists_ThrowsException() {
		CreateExamCenterRequestDTO dto = new CreateExamCenterRequestDTO();
		dto.setCenterName("existing-center-name");
		when(mockExamCenterRepository.existsByCenterName(anyString())).thenReturn(true);
		IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
				() -> examSchedulingServiceImpl.createExamCenter(dto));
		verify(mockExamCenterRepository).existsByCenterName(anyString());
		assertTrue(ex.getMessage().equals("An Exam Center with this Name already exists."));
	}

	@Test
	void test_createExamCenter_NewCenter_SavesAndReturnsCorrectly() {
		CreateExamCenterRequestDTO dto = new CreateExamCenterRequestDTO();
		dto.setCenterName("A B College");
		dto.setCenterAddress("M.G.Road");
		dto.setCenterCity("Pune");
		dto.setCenterState("Maharashtra");
		dto.setCenterPinCode("123456");
		dto.setCenterContactPerson("Mr. A B. Mane");
		dto.setCenterContactPhone("9876543210");
		ExamCenter savedExamCenter = ExamCenter.builder()
				.centerId(1L)
				.centerName("A B College")
				.addressLine("M.G.Road")
				.city("Pune")
				.state("Maharashtra")
				.pinCode("123456")
				.contactPerson("Mr. A B. Mane")
				.contactPhone("9876543210")
				.build();
		when(mockExamCenterRepository.existsByCenterName(anyString())).thenReturn(false);
		when(mockExamCenterRepository.save(any(ExamCenter.class))).thenReturn(savedExamCenter);
		CreateExamCenterResponseDTO createExamCenterResponseDTO =
				examSchedulingServiceImpl.createExamCenter(dto);
		verify(mockExamCenterRepository).existsByCenterName(anyString());

		ArgumentCaptor<ExamCenter> argCaptorExamCenter =
				ArgumentCaptor.forClass(ExamCenter.class);
		verify(mockExamCenterRepository, times(1)).save(argCaptorExamCenter.capture());
		ExamCenter toSaveExamCenter = argCaptorExamCenter.getValue();
		assertEquals("A B College", toSaveExamCenter.getCenterName());
		assertEquals("M.G.Road", toSaveExamCenter.getAddressLine());
		assertEquals("Pune", toSaveExamCenter.getCity());
		assertEquals("Maharashtra", toSaveExamCenter.getState());
		assertEquals("123456", toSaveExamCenter.getPinCode());
		assertEquals("Mr. A B. Mane", toSaveExamCenter.getContactPerson());
		assertEquals("9876543210", toSaveExamCenter.getContactPhone());

		assertEquals(1L, createExamCenterResponseDTO.getCenterId());
	}

	@Test
	void test_addExamSlot_ExamCenterNotExists_RaiseExecption() {
		when(mockExamCenterRepository.existsById(anyLong()))
			.thenReturn(false);
		IllegalArgumentException arg = assertThrows(IllegalArgumentException.class,
				() -> examSchedulingServiceImpl.addExamSlot(1L, new CreateExamSlotRequest()));
		verify(mockExamCenterRepository).existsById(anyLong());
		assertEquals("Exam Center with id 1 not found.", arg.getMessage());
	}

	@Test
	void test_addExamSlot_ExamCenterDateStartTimeEndTime_Exists_RaiseException() {
		when(mockExamCenterRepository.existsById(anyLong())).thenReturn(true);
		when(mockExamSlotRepository.existsByExamCenter_CenterIdAndExamDateAndStartTimeAndEndTime(
				anyLong(), any(LocalDate.class), any(LocalTime.class), any(LocalTime.class)))
			.thenReturn(true);
		CreateExamSlotRequest slotRequest = CreateExamSlotRequest.builder()
				.examDate(LocalDate.of(2025, 12, 01))
				.examStartTime(LocalTime.of(11, 0))
				.examEndTime(LocalTime.of(12, 0))
				.build();
		IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
				() -> examSchedulingServiceImpl.addExamSlot(1L, slotRequest));
		verify(mockExamCenterRepository).existsById(anyLong());
		verify(mockExamSlotRepository).existsByExamCenter_CenterIdAndExamDateAndStartTimeAndEndTime(
				anyLong(), any(LocalDate.class), any(LocalTime.class), any(LocalTime.class));
		assertEquals("Exam Slot with centerId=1, date=2025-12-01, start time=11:00, end time=12:00 already exists.", ex.getMessage());
	}

	@Test
	void test_addExamSlot_New_ExamCenterDateStartTimeEndTime_Save() {
		when(mockExamCenterRepository.existsById(anyLong())).thenReturn(true);
		when(mockExamSlotRepository.existsByExamCenter_CenterIdAndExamDateAndStartTimeAndEndTime(
				anyLong(), any(LocalDate.class), any(LocalTime.class), any(LocalTime.class)))
			.thenReturn(false);
		when(mockEntityManager.getReference(eq(ExamCenter.class), anyLong()))
			.thenReturn(ExamCenter.builder().centerId(1L).build());
		CreateExamSlotRequest slotRequest = CreateExamSlotRequest.builder()
				.examDate(LocalDate.of(2025, 12, 01))
				.examStartTime(LocalTime.of(11, 0))
				.examEndTime(LocalTime.of(12, 0))
				.totalSeats(25)
				.build();
		ExamSlot savedExamSlot = ExamSlot.builder()
				.slotId(1L)
				.examDate(LocalDate.of(2025, 12, 01))
				.startTime(LocalTime.of(11, 0))
				.endTime(LocalTime.of(12, 0))
				.totalSeats(25)
				.build();
		when(mockExamSlotRepository.save(any(ExamSlot.class)))
			.thenReturn(savedExamSlot);
		examSchedulingServiceImpl.addExamSlot(1L, slotRequest);
		verify(mockExamCenterRepository).existsById(anyLong());
		verify(mockExamSlotRepository).existsByExamCenter_CenterIdAndExamDateAndStartTimeAndEndTime(
				anyLong(), any(LocalDate.class), any(LocalTime.class), any(LocalTime.class));
		verify(mockEntityManager).getReference(eq(ExamCenter.class), anyLong());
		ArgumentCaptor<ExamSlot> argCaptorExamSlot =
				ArgumentCaptor.forClass(ExamSlot.class);
		verify(mockExamSlotRepository).save(argCaptorExamSlot.capture());
		ExamSlot toSaveExamSlot = argCaptorExamSlot.getValue();
		assertEquals(1L, toSaveExamSlot.getExamCenter().getCenterId());
		assertTrue(toSaveExamSlot.getExamDate().equals(LocalDate.of(2025, 12, 01)));
		assertTrue(toSaveExamSlot.getStartTime().equals(LocalTime.of(11, 0)));
		assertTrue(toSaveExamSlot.getEndTime().equals(LocalTime.of(12, 0)));
		assertEquals(25, toSaveExamSlot.getTotalSeats());
	}
}
