package com.neec.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.neec.dto.CreateExamCenterRequestDTO;
import com.neec.dto.CreateExamCenterResponseDTO;
import com.neec.entity.ExamCenter;
import com.neec.repository.ExamCenterRepository;

@ExtendWith(MockitoExtension.class)
public class ExamSchedulingServiceTest {
	@Mock
	private ExamCenterRepository mockExamCenterRepository;
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
}
