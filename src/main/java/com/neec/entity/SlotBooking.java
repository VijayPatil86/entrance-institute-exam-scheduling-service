package com.neec.entity;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import com.neec.enums.SlotBookingStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Builder @NoArgsConstructor @AllArgsConstructor @FieldDefaults(level = AccessLevel.PRIVATE) @Getter
@Entity
@Table(
		name = "SLOT_BOOKINGS",
		uniqueConstraints = {@UniqueConstraint(columnNames = {"USER_ID", "SLOT_ID"})}
	  )
public class SlotBooking {
	@Column(name = "BOOKING_ID", insertable = false, updatable = false, unique = true, nullable = false)
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long bookingId;

	@Column(name = "USER_ID", insertable = true, updatable = false, nullable = false)
	Long userId;

	@Column(name = "BOOKING_TIME", insertable = true, updatable = false, nullable = false, unique = false)
	OffsetDateTime bookingTime;

	@Column(name = "BOOKING_STATUS", insertable = true, updatable = true, nullable = false, unique = false)
	@Enumerated(EnumType.STRING)
	SlotBookingStatus slotBookingStatus;

	@Column(name = "UPDATED_AT", insertable = true, updatable = true, nullable = false, unique = false)
	OffsetDateTime updatedAt;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SLOT_ID", referencedColumnName = "SLOT_ID", insertable = true, updatable = false, nullable = false)
	ExamSlot examSlot;

	@PrePersist
	void onSave() {
		OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
		this.bookingTime = now;
		this.updatedAt = now;
	}

	@PreUpdate
	void onUpdate() {
		this.updatedAt = OffsetDateTime.now(ZoneOffset.UTC);
	}
}
