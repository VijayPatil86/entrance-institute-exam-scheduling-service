package com.neec.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Builder @AllArgsConstructor @NoArgsConstructor @FieldDefaults(level = AccessLevel.PRIVATE) @Getter
public class CustomPrincipal {
	String subject;
	String emailAddress;
}
