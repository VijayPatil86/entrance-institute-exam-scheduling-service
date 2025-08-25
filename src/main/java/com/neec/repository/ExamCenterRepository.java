package com.neec.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.neec.entity.ExamCenter;

public interface ExamCenterRepository extends JpaRepository<ExamCenter, Long> {
	boolean existsByCenterName(String centerName);
	@Query(value = "SELECT DISTINCT e.city FROM ExamCenter e ORDER BY e.city")
	List<String> findAllCities();
}
