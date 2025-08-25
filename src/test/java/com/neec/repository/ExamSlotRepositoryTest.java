package com.neec.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;

import com.neec.entity.ExamCenter;
import com.neec.entity.ExamSlot;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class ExamSlotRepositoryTest {
	@Autowired
	private ExamSlotRepository examSlotRepository;
	@Autowired
	private ExamCenterRepository examCenterRepository;

	@DirtiesContext
	@Test
	void test_findAvailableSlotsByCity() {
		ExamCenter examCenter = ExamCenter.builder().centerName("A B College").addressLine("A. B. Road").city("Pune")
				.state("Maharashtra").pinCode("147258").contactPerson("Mr. R.K. Sane")
				.contactPhone("9876543210").build();
		ExamCenter savedExamCenter = examCenterRepository.save(examCenter);
		List<ExamSlot> listExamSlots = List.of(
			ExamSlot.builder()
				.examCenter(savedExamCenter)
				.examDate(LocalDate.of(2025, 01, 01))
				.startTime(LocalTime.of(10, 0))
				.endTime(LocalTime.of(10, 30))
				.totalSeats(10)
				.bookedSeats(5)
			.build(),
			ExamSlot.builder()
				.examCenter(savedExamCenter)
				.examDate(LocalDate.of(2025, 01, 01))
				.startTime(LocalTime.of(11, 0))
				.endTime(LocalTime.of(11, 30))
				.totalSeats(20)
				.bookedSeats(15)
			.build(),
			ExamSlot.builder()
				.examCenter(savedExamCenter)
				.examDate(LocalDate.of(2025, 01, 01))
				.startTime(LocalTime.of(12, 0))
				.endTime(LocalTime.of(12, 30))
				.totalSeats(30)
				.bookedSeats(30)
			.build()
		);
		examSlotRepository.saveAll(listExamSlots);

		examCenter = ExamCenter.builder().centerName("C D College").addressLine("C. D. Road").city("Mumbai")
				.state("Maharashtra").pinCode("147258").contactPerson("Mr. R.K. Sane")
				.contactPhone("9876543210").build();
		savedExamCenter = examCenterRepository.save(examCenter);
		listExamSlots = List.of(
			ExamSlot.builder()
				.examCenter(savedExamCenter)
				.examDate(LocalDate.of(2025, 01, 01))
				.startTime(LocalTime.of(10, 0))
				.endTime(LocalTime.of(10, 30))
				.totalSeats(50)
				.bookedSeats(5)
			.build(),
			ExamSlot.builder()
				.examCenter(savedExamCenter)
				.examDate(LocalDate.of(2025, 01, 01))
				.startTime(LocalTime.of(11, 0))
				.endTime(LocalTime.of(11, 30))
				.totalSeats(10)
				.bookedSeats(10)
			.build(),
			ExamSlot.builder()
				.examCenter(savedExamCenter)
				.examDate(LocalDate.of(2025, 01, 01))
				.startTime(LocalTime.of(12, 0))
				.endTime(LocalTime.of(12, 30))
				.totalSeats(50)
				.bookedSeats(50)
			.build()
		);
		examSlotRepository.saveAll(listExamSlots);

		List<ExamSlot> availableSlotsByCity =
				examSlotRepository.findAvailableSlotsByCity("Pune");
		assertEquals(2, availableSlotsByCity.size(), "Expected 2 available slots in Pune");
		assertEquals("Pune", availableSlotsByCity.get(0).getExamCenter().getCity(), "Expected Exam Center as Pune");

		availableSlotsByCity =
				examSlotRepository.findAvailableSlotsByCity("Mumbai");
		assertEquals(1, availableSlotsByCity.size(), "Expected 1 available slots in Mumbai");
		assertEquals("Mumbai", availableSlotsByCity.get(0).getExamCenter().getCity(), "Expected Exam Center as Mumbai");
	}
}
