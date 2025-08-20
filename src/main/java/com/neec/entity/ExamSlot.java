package com.neec.entity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Builder @AllArgsConstructor @NoArgsConstructor @FieldDefaults(level = AccessLevel.PRIVATE) @Getter @Setter
@Entity
@Table(name = "EXAM_SLOTS")
public class ExamSlot {
	@Column(name = "SLOT_ID")
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long slotId;

	@Column(name = "EXAM_DATE", insertable = true, updatable = true, unique = false, nullable = false)
	private LocalDate examDate;

	@Column(name = "START_TIME", insertable = true, updatable = true, unique = false, nullable = false)
	private LocalTime startTime;

	@Column(name = "END_TIME", insertable = true, updatable = true, unique = false, nullable = false)
	private LocalTime endTime;

	@Column(name = "TOTAL_SEATS", insertable = true, updatable = true, unique = false, nullable = false)
	private int totalSeats;

	@Column(name = "BOOKED_SEATS", insertable = true, updatable = true, unique = false, nullable = false)
	private int bookedSeats;

	@Column(name = "CREATED_AT", insertable = true, updatable = false, unique = false, nullable = false)
	private OffsetDateTime createdAt;

	@Column(name = "UPDATED_AT", insertable = true, updatable = true, unique = false, nullable = false)
	private OffsetDateTime updatedAt;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CENTER_ID", referencedColumnName = "CENTER_ID", insertable = true, updatable = true, nullable = false, unique = false)
	private ExamCenter examCenter;

	@PrePersist
	void beforeSave() {
		OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
		this.createdAt = now;
		this.updatedAt = now;
	}

	@PreUpdate
	void beforeUpdate() {
		this.updatedAt = OffsetDateTime.now(ZoneOffset.UTC);
	}
}
