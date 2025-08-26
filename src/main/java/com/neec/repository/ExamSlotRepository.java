package com.neec.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import com.neec.entity.ExamSlot;

import jakarta.persistence.LockModeType;

public interface ExamSlotRepository extends JpaRepository<ExamSlot, Long> {
	boolean existsByExamCenter_CenterIdAndExamDateAndStartTimeAndEndTime(
		Long centerId, LocalDate examDate, LocalTime startTime, LocalTime endTime
	);
	@Query(value = "SELECT es FROM ExamSlot es JOIN FETCH es.examCenter ec WHERE ec.city = :city AND es.bookedSeats < es.totalSeats")
	List<ExamSlot> findAvailableSlotsByCity(String city);
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	Optional<ExamSlot> findWithLockingBySlotId(Long slotId);
}
