package com.neec.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
@RequestMapping("/api/v1")
public class ExamSchedulingController {
	@PostMapping("/admin/centers")
	ResponseEntity<?> createCenter(){
		return ResponseEntity.ok(Map.of("status", "pass"));
	}
}
