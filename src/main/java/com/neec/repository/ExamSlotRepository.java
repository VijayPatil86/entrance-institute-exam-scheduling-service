package com.neec.repository;

import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.data.jpa.repository.JpaRepository;

import com.neec.entity.ExamSlot;

public interface ExamSlotRepository extends JpaRepository<ExamSlot, Long> {
	boolean existsByExamCenter_CenterIdAndExamDateAndStartTimeAndEndTime(
		Long centerId, LocalDate examDate, LocalTime startTime, LocalTime endTime
	);
}
