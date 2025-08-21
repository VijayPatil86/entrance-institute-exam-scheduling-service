package com.neec.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.neec.entity.ExamCenter;

public interface ExamCenterRepository extends JpaRepository<ExamCenter, Long> {
	boolean existsByCenterName(String centerName);
}
