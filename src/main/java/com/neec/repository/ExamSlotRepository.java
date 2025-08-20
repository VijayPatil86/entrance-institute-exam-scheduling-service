package com.neec.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.neec.entity.ExamSlot;

public interface ExamSlotRepository extends JpaRepository<ExamSlot, Long> {
}
