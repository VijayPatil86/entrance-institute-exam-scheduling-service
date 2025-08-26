package com.neec.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.neec.entity.SlotBooking;

public interface SlotBookingRepository extends JpaRepository<SlotBooking, Long> {

}
