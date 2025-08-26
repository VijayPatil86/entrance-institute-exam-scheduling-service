package com.neec.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor @NoArgsConstructor @FieldDefaults(level = AccessLevel.PRIVATE) @Getter @Setter
public class CreateSlotBookingRequestDTO {
	@NotNull(message = "slot id can not be null")
	Long slotId;
}
