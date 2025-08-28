package com.neec.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.DirtiesContext;

import com.neec.entity.ExamCenter;
import com.neec.entity.ExamSlot;
import com.neec.entity.SlotBooking;
import com.neec.enums.SlotBookingStatus;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class SlotBookingRepositoryTest {
	@Autowired
	private ExamCenterRepository examCenterRepository;
	@Autowired
	private ExamSlotRepository examSlotRepository;
	@Autowired
	private SlotBookingRepository slotBookingRepository;

	@DirtiesContext
	@Test
	void test_bookSlot_Success() {
		ExamCenter examCenter_Pune = ExamCenter.builder().centerName("A B College").addressLine("A. B. Road").city("Pune")
				.state("Maharashtra").pinCode("147258").contactPerson("Mr. R.K. Sane")
				.contactPhone("9876543210").build();
		ExamCenter savedExamCenter_Pune = examCenterRepository.save(examCenter_Pune);
		ExamSlot examSlot_Pune = ExamSlot.builder()
				.examCenter(savedExamCenter_Pune)
				.examDate(LocalDate.of(2025, 01, 01))
				.startTime(LocalTime.of(10, 0))
				.endTime(LocalTime.of(10, 30))
				.totalSeats(10)
				.bookedSeats(5)
				.build();
		ExamSlot savedExamSlot = examSlotRepository.save(examSlot_Pune);
		SlotBooking slotBooking = SlotBooking.builder()
				.userId(102L)
				.bookingTime(OffsetDateTime.now(ZoneOffset.UTC))
				.slotBookingStatus(SlotBookingStatus.CONFIRMED)
				.examSlot(savedExamSlot)
				.build();
		SlotBooking savedSlotBooking = slotBookingRepository.save(slotBooking);
		assertNotNull(savedSlotBooking);
		assertEquals(SlotBookingStatus.CONFIRMED, savedSlotBooking.getSlotBookingStatus());
		assertEquals(savedSlotBooking.getExamSlot().getSlotId(), slotBooking.getExamSlot().getSlotId());
		/*SlotBooking repeatSlotBooking = SlotBooking.builder()
				.userId(102L)
				.bookingTime(OffsetDateTime.now(ZoneOffset.UTC))
				.slotBookingStatus(SlotBookingStatus.CONFIRMED)
				.examSlot(savedExamSlot)
				.build();
		slotBookingRepository.save(repeatSlotBooking);*/
	}

	@DirtiesContext
	@Test
	void test_bookSlot_RepeatSameSlotBooking() {
		ExamCenter examCenter_Pune = ExamCenter.builder().centerName("A B College").addressLine("A. B. Road").city("Pune")
				.state("Maharashtra").pinCode("147258").contactPerson("Mr. R.K. Sane")
				.contactPhone("9876543210").build();
		ExamCenter savedExamCenter_Pune = examCenterRepository.save(examCenter_Pune);
		ExamSlot examSlot_Pune = ExamSlot.builder()
				.examCenter(savedExamCenter_Pune)
				.examDate(LocalDate.of(2025, 01, 01))
				.startTime(LocalTime.of(10, 0))
				.endTime(LocalTime.of(10, 30))
				.totalSeats(10)
				.bookedSeats(5)
				.build();
		ExamSlot savedExamSlot = examSlotRepository.save(examSlot_Pune);
		SlotBooking slotBooking = SlotBooking.builder()
				.userId(102L)
				.bookingTime(OffsetDateTime.now(ZoneOffset.UTC))
				.slotBookingStatus(SlotBookingStatus.CONFIRMED)
				.examSlot(savedExamSlot)
				.build();
		SlotBooking savedSlotBooking = slotBookingRepository.save(slotBooking);
		SlotBooking repeatSlotBooking = SlotBooking.builder()
				.userId(102L)
				.bookingTime(OffsetDateTime.now(ZoneOffset.UTC))
				.slotBookingStatus(SlotBookingStatus.CONFIRMED)
				.examSlot(savedExamSlot)
				.build();
		DataIntegrityViolationException ex =
				assertThrows(DataIntegrityViolationException.class,
						() -> slotBookingRepository.save(repeatSlotBooking));
		assertNotNull(ex);
		assertTrue(ex.getCause().getMessage().contains("duplicate key value violates unique constraint \"uk_slot_id_user_id\""));
	}
}
