package com.neec.repository;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.neec.entity.ExamCenter;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class ExamCenterRepositoryTest {
	@Autowired
	private ExamCenterRepository examCenterRepository;

	@Test
	void test_existsByCenterName_NonExisting_ExamCenterName() {
		boolean isExamCenterExists = examCenterRepository.existsByCenterName("non-existing-exam-center-name");
		assertTrue(isExamCenterExists == false);
	}

	@Test
	void test_existsByCenterName_Existing_ExamCenterName() {
		ExamCenter examCenter = ExamCenter.builder()
				.centerName("A B College")
				.addressLine("M. G. Road")
				.city("Pune")
				.state("Maharashtra")
				.pinCode("123456")
				.contactPerson("Mr. R.K. More")
				.contactPhone("9876543210")
				.build();
		ExamCenter savedExamCenter = examCenterRepository.save(examCenter);
		boolean isExamCenterExists = examCenterRepository.existsByCenterName("A B College");
		assertTrue(isExamCenterExists);
	}
}
