package com.neec.entity;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "EXAM_CENTERS")
public class ExamCenter {
	@Column(name = "CENTER_ID")
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long centerId;

	@Column(name = "CENTER_NAME", insertable = true, updatable = true, unique = true, nullable = false)
	private String centerName;

	@Column(name = "ADDRESS_LINE", insertable = true, updatable = true, unique = false, nullable = false)
	private String addressLine;

	@Column(name = "CITY", insertable = true, updatable = true, unique = false, nullable = false)
	private String city;

	@Column(name = "STATE", insertable = true, updatable = true, unique = false, nullable = false)
	private String state;

	@Column(name = "PIN_CODE", insertable = true, updatable = true, unique = false, nullable = false)
	private String pinCode;

	@Column(name = "CONTACT_PERSON", insertable = true, updatable = true, unique = false, nullable = false)
	private String contactPerson;

	@Column(name = "CONTACT_PHONE", insertable = true, updatable = true, unique = false, nullable = false)
	private String contactPhone;

	@Column(name = "CREATED_AT", insertable = true, updatable = false, unique = false, nullable = false)
	private OffsetDateTime createdAt;

	@Column(name = "UPDATED_AT", insertable = true, updatable = true, unique = false, nullable = false)
	private OffsetDateTime updatedAt;

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
