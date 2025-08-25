package com.neec.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

	@Test
	void test_findAllCities_Returns_DistinctCityNames_NaturalOrder() {
		List<ExamCenter> listExamCentersToSave = sampleExamCenters();
		examCenterRepository.saveAll(listExamCentersToSave);
		List<String> listCityNames = examCenterRepository.findAllCities();
		long countDistinctCityNames = listCityNames.stream().distinct().count();
		assertEquals(countDistinctCityNames, listCityNames.size(), "duplicate city names found");
		List<String> copyOfCityNames = new ArrayList<>(listCityNames);
		Collections.sort(copyOfCityNames);
		assertEquals(copyOfCityNames, listCityNames, "city names are not in natural order");
	}

	List<ExamCenter> sampleExamCenters() {
		return List.of(
				ExamCenter.builder().centerName("A B College").addressLine("A. B. Road").city("Pune")
						.state("Maharashtra").pinCode("147258").contactPerson("Mr. R.K. Sane")
						.contactPhone("9876543210").build(),
				ExamCenter.builder().centerName("C D College").addressLine("C. D. Road").city("Mumbai")
						.state("Maharashtra").pinCode("369875").contactPerson("Mr. R.K. Mane")
						.contactPhone("8876543210").build(),
				ExamCenter.builder().centerName("E F College").addressLine("E. F. Road").city("Pune")
						.state("Maharashtra").pinCode("100236").contactPerson("Mr. R.K. More")
						.contactPhone("7876543210").build(),
				ExamCenter.builder().centerName("G H College").addressLine("G. H. Road").city("Thane")
						.state("Maharashtra").pinCode("556644").contactPerson("Mr. R.K. Kore")
						.contactPhone("9876543211").build());
	}
}
