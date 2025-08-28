package com.neec.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import com.neec.dto.CreateExamCenterRequestDTO;
import com.neec.dto.CreateExamCenterResponseDTO;
import com.neec.dto.CreateExamSlotRequest;
import com.neec.dto.CreateSlotBookingResponseDTO;
import com.neec.dto.ExamCenterResponseDTO;
import com.neec.dto.ExamSlotResponse;
import com.neec.entity.ExamCenter;
import com.neec.entity.ExamSlot;
import com.neec.entity.SlotBooking;
import com.neec.enums.SlotBookingStatus;
import com.neec.repository.ExamCenterRepository;
import com.neec.repository.ExamSlotRepository;
import com.neec.repository.SlotBookingRepository;

import jakarta.persistence.EntityManager;

@ExtendWith(MockitoExtension.class)
public class ExamSchedulingServiceTest {
	@Mock
	private ExamCenterRepository mockExamCenterRepository;
	@Mock
	private ExamSlotRepository mockExamSlotRepository;
	@Mock
	private EntityManager mockEntityManager;
	@Mock
	private SlotBookingRepository mockSlotBookingRepository;
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

	@Test
	void test_getAllExamCenters() {
		List<ExamCenter> listExamCenters = List.of(
			buildAndReturn_ExamCenter(1L, "College of Commerce", "M.G.Road", "Thane", "Maharashtra", "123456", "Mr. More", "9876543210"),
			buildAndReturn_ExamCenter(2L, "College of Arts", "C.D. Road", "Delhi", "Delhi", "456789", "Mr. Sane", "8876543210")
		);
		when(mockExamCenterRepository.findAll()).thenReturn(listExamCenters);
		List<ExamCenterResponseDTO> listExamCenterResponseDTOs =
				examSchedulingServiceImpl.getAllCenters();
		verify(mockExamCenterRepository).findAll();
		assertNotNull(listExamCenterResponseDTOs);
		assertNotNull(listExamCenterResponseDTOs.get(0));
		ExamCenterResponseDTO dto = listExamCenterResponseDTOs.get(0);
		assertEquals(1L, dto.getCenterId());
		assertEquals("College of Commerce", dto.getCenterName());
		assertEquals("M.G.Road", dto.getCenterAddress());
		assertEquals("Thane", dto.getCenterCity());
		assertEquals("Maharashtra", dto.getCenterState());
		assertEquals("123456", dto.getCenterPinCode());
		assertEquals("Mr. More", dto.getCenterContactPerson());
		assertEquals("9876543210", dto.getCenterContactPhone());
		assertNotNull(listExamCenterResponseDTOs.get(1));
		dto = listExamCenterResponseDTOs.get(1);
		assertEquals(2L, dto.getCenterId());
		assertEquals("College of Arts", dto.getCenterName());
		assertEquals("C.D. Road", dto.getCenterAddress());
		assertEquals("Delhi", dto.getCenterCity());
		assertEquals("Delhi", dto.getCenterState());
		assertEquals("456789", dto.getCenterPinCode());
		assertEquals("Mr. Sane", dto.getCenterContactPerson());
		assertEquals("8876543210", dto.getCenterContactPhone());
	}

	private ExamCenter buildAndReturn_ExamCenter(Long centerId, String centerName,
			String centerAddress, String centerCity, String centerState,
			String centerPinCode, String centerContactPerson, String centerContactPhone) {
		return ExamCenter.builder()
				.centerId(centerId)
				.centerName(centerName)
				.addressLine(centerAddress)
				.city(centerCity)
				.state(centerState)
				.pinCode(centerPinCode)
				.contactPerson(centerContactPerson)
				.contactPhone(centerContactPhone)
				.build();
	}

	@Test
	void test_getAvailableCities_DistinctCityNames_NaturalOrder() {
		List<String> listCityNames = List.of("Mumbai", "Pune", "Thane");
		when(mockExamCenterRepository.findAllCities()).thenReturn(listCityNames);
		List<String> returnedListCityNames = examSchedulingServiceImpl.getAvailableCities();
		verify(mockExamCenterRepository).findAllCities();
		assertEquals(listCityNames, returnedListCityNames, "Returned city list is incorrect — content or order does not match expected");
	}

	@Test
	void test_findAvailableSlotsForGivenCity() {
		ExamCenter examCenter_Pune = ExamCenter.builder().centerName("A B College").addressLine("A. B. Road").city("Pune")
				.state("Maharashtra").pinCode("147258").contactPerson("Mr. R.K. Sane")
				.contactPhone("9876543210").build();
		List<ExamSlot> listExamSlots = List.of(
				ExamSlot.builder()
					.examCenter(examCenter_Pune)
					.examDate(LocalDate.of(2025, 01, 01))
					.startTime(LocalTime.of(10, 0))
					.endTime(LocalTime.of(10, 30))
					.totalSeats(10)
					.bookedSeats(5)
				.build(),
				ExamSlot.builder()
					.examCenter(examCenter_Pune)
					.examDate(LocalDate.of(2025, 01, 01))
					.startTime(LocalTime.of(11, 0))
					.endTime(LocalTime.of(11, 30))
					.totalSeats(20)
					.bookedSeats(15)
				.build()
			);
		when(mockExamSlotRepository.findAvailableSlotsByCity(anyString()))
			.thenReturn(listExamSlots);
		List<ExamSlotResponse> listExamSlotResponse =
				examSchedulingServiceImpl.findAvailableSlots("Pune");
		verify(mockExamSlotRepository).findAvailableSlotsByCity(anyString());
		assertEquals(2, listExamSlotResponse.size(), "Expected exactly 2 available slots");
		ExamSlotResponse firstSlot = listExamSlotResponse.get(0);
		assertEquals(5, firstSlot.getAvailableSeats(), "Available seats calculation is icorrect for first slot");
		assertEquals("Pune", firstSlot.getCenterCity(), "Exepcted slot city as Pune");
	}

	@Test
	void test_bookSlot_ExamSlot_NotExists() {
		when(mockExamSlotRepository.findWithLockingBySlotId(anyLong()))
			.thenReturn(Optional.empty());
		IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
				() -> examSchedulingServiceImpl.bookSlot(1L, 2L));
		verify(mockExamSlotRepository).findWithLockingBySlotId(anyLong());
		assertEquals("Exam Slot with id 2 not found.", ex.getMessage());
		verify(mockExamSlotRepository, never()).save(any(ExamSlot.class));
		verify(mockSlotBookingRepository, never()).save(any(SlotBooking.class));
	}

	@Test
	void test_bookSlot_SlotAlreadyFull() {
		ExamSlot examSlot = ExamSlot.builder()
				.slotId(1L)
				.bookedSeats(10)
				.totalSeats(10)
				.build();
		when(mockExamSlotRepository.findWithLockingBySlotId(anyLong()))
			.thenReturn(Optional.of(examSlot));
		IllegalStateException ex = assertThrows(IllegalStateException.class,
				() -> examSchedulingServiceImpl.bookSlot(1L, 1L));
		verify(mockExamSlotRepository).findWithLockingBySlotId(anyLong());
		assertEquals("this exam slot is already full", ex.getMessage());
		// Verify that no database write operations were attempted
		verify(mockExamSlotRepository, never()).save(any(ExamSlot.class));
		verify(mockSlotBookingRepository, never()).save(any(SlotBooking.class));
	}

	@Test
	void test_bookSlot_Success() {
		ExamCenter examCenter_Pune = ExamCenter.builder().centerName("A B College").addressLine("A. B. Road").city("Pune")
				.state("Maharashtra").pinCode("147258").contactPerson("Mr. R.K. Sane")
				.contactPhone("9876543210").build();
		ExamSlot examSlot = ExamSlot.builder()
				.slotId(1L)
				.examCenter(examCenter_Pune)
				.examDate(LocalDate.of(2025, 12, 02))
				.startTime(LocalTime.of(10, 0))
				.endTime(LocalTime.of(10, 30))
				.totalSeats(10)
				.bookedSeats(5)
				.build();
		when(mockExamSlotRepository.findWithLockingBySlotId(anyLong()))
			.thenReturn(Optional.of(examSlot));
		ExamSlot savedExamSlot = ExamSlot.builder().build();
		when(mockExamSlotRepository.save(any(ExamSlot.class)))
			.thenReturn(savedExamSlot);
		SlotBooking savedSlotBooking = SlotBooking.builder()
				.bookingId(1L)
				.slotBookingStatus(SlotBookingStatus.CONFIRMED)
				.bookingTime(OffsetDateTime.of(LocalDate.of(2025, 12, 01), LocalTime.of(10, 0), ZoneOffset.UTC))
				.build();
		when(mockSlotBookingRepository.save(any(SlotBooking.class)))
			.thenReturn(savedSlotBooking);
		CreateSlotBookingResponseDTO createSlotBookingResponseDTO =
				examSchedulingServiceImpl.bookSlot(1L, 1L);
		verify(mockExamSlotRepository).findWithLockingBySlotId(anyLong());

		ArgumentCaptor<ExamSlot> argCaptorExamSlot =
				ArgumentCaptor.forClass(ExamSlot.class);
		verify(mockExamSlotRepository).save(argCaptorExamSlot.capture());
		ExamSlot toSaveExamSlot = argCaptorExamSlot.getValue();
		assertNotNull(toSaveExamSlot);
		assertEquals(1L, toSaveExamSlot.getSlotId());
		assertEquals(6, toSaveExamSlot.getBookedSeats());

		ArgumentCaptor<SlotBooking> argCaptorSlotBooking =
				ArgumentCaptor.forClass(SlotBooking.class);
		verify(mockSlotBookingRepository).save(argCaptorSlotBooking.capture());
		SlotBooking toSaveSlotBooking = argCaptorSlotBooking.getValue();
		assertNotNull(toSaveSlotBooking);
		assertEquals(1L, toSaveSlotBooking.getUserId());
		assertEquals(1L, toSaveSlotBooking.getExamSlot().getSlotId());
		assertEquals(SlotBookingStatus.CONFIRMED, toSaveSlotBooking.getSlotBookingStatus());

		assertEquals(1L, createSlotBookingResponseDTO.getBookingId());
		assertEquals(SlotBookingStatus.CONFIRMED, createSlotBookingResponseDTO.getSlotBookingStatus());
		assertEquals(1L, createSlotBookingResponseDTO.getSlotId());
		assertEquals(LocalDate.of(2025, 12, 02), createSlotBookingResponseDTO.getExamDate());
		assertEquals(LocalTime.of(10, 0), createSlotBookingResponseDTO.getExamStartTime());
		assertEquals(LocalTime.of(10, 30), createSlotBookingResponseDTO.getExamEndTime());
		assertEquals("A B College", createSlotBookingResponseDTO.getCenterName());
		assertEquals("A. B. Road", createSlotBookingResponseDTO.getCenterAddress());
		assertEquals("Pune", createSlotBookingResponseDTO.getCenterCity());
		assertEquals(toSaveSlotBooking.getBookingTime(), createSlotBookingResponseDTO.getSlotBookingDateTime());
	}

	@Test
	void test_bookSlot_BookSameExamSlotAgain() {
		ExamCenter examCenter_Pune = ExamCenter.builder().centerName("A B College").addressLine("A. B. Road").city("Pune")
				.state("Maharashtra").pinCode("147258").contactPerson("Mr. R.K. Sane")
				.contactPhone("9876543210").build();
		ExamSlot examSlot = ExamSlot.builder()
				.slotId(1L)
				.examCenter(examCenter_Pune)
				.examDate(LocalDate.of(2025, 12, 02))
				.startTime(LocalTime.of(10, 0))
				.endTime(LocalTime.of(10, 30))
				.totalSeats(10)
				.bookedSeats(5)
				.build();
		when(mockExamSlotRepository.findWithLockingBySlotId(anyLong()))
			.thenReturn(Optional.of(examSlot));
		ExamSlot savedExamSlot = ExamSlot.builder().build();
		when(mockExamSlotRepository.save(any(ExamSlot.class)))
			.thenReturn(savedExamSlot);
		when(mockSlotBookingRepository.save(any(SlotBooking.class)))
			.thenThrow(new DataIntegrityViolationException("duplicate key value violates unique constraint \"uk_slot_id_user_id\""));
		DataIntegrityViolationException ex = assertThrows(DataIntegrityViolationException.class,
				() -> examSchedulingServiceImpl.bookSlot(1L, 1L));
		assertNotNull(ex);
		assertTrue(ex.getMessage().contains("duplicate key value violates unique constraint \"uk_slot_id_user_id\""));
	}
}
