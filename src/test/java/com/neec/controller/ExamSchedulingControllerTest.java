package com.neec.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.neec.util.JwtUtil;

@WebMvcTest(controllers = ExamSchedulingController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ExamSchedulingControllerTest {
	@Autowired
	private MockMvc mockMvc;
	@MockitoBean
	private JwtUtil mockJwtUtil;

	@Test
	void test_createCenter() throws Exception {
		RequestBuilder request = MockMvcRequestBuilders.post("/api/v1/admin/centers");
		MvcResult result = mockMvc.perform(request)
				.andDo(print())
				.andReturn();
		assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
	}
}
