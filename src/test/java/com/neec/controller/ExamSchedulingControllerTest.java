package com.neec.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neec.dto.CreateExamCenterRequestDTO;
import com.neec.dto.CreateExamCenterResponseDTO;
import com.neec.dto.CreateExamSlotRequest;
import com.neec.service.ExamSchedulingService;
import com.neec.util.JwtUtil;

@WebMvcTest(controllers = ExamSchedulingController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ExamSchedulingControllerTest {
	@Autowired
	private MockMvc mockMvc;
	@MockitoBean
	private JwtUtil mockJwtUtil;
	@MockitoBean
	private ExamSchedulingService mockExamSchedulingService;
	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void test_createExamCenter_NewCenter_SavesAndReturnsCorrectly() throws Exception {
		CreateExamCenterRequestDTO createExamCenterRequestDTO = new CreateExamCenterRequestDTO();
		createExamCenterRequestDTO.setCenterName("A B College");
		createExamCenterRequestDTO.setCenterAddress("M.G.Road");
		createExamCenterRequestDTO.setCenterCity("Pune");
		createExamCenterRequestDTO.setCenterState("Maharashtra");
		createExamCenterRequestDTO.setCenterPinCode("123456");
		createExamCenterRequestDTO.setCenterContactPerson("Mr. A. K. More");
		createExamCenterRequestDTO.setCenterContactPhone("9876543210");

		CreateExamCenterResponseDTO createExamCenterResponseDTO = new CreateExamCenterResponseDTO();
		createExamCenterResponseDTO.setCenterId(1L);
		createExamCenterResponseDTO.setCenterName("A B College");
		createExamCenterResponseDTO.setCenterAddress("M.G.Road");
		createExamCenterResponseDTO.setCenterCity("Pune");
		createExamCenterResponseDTO.setCenterState("Maharashtra");
		createExamCenterResponseDTO.setCenterPinCode("123456");
		createExamCenterResponseDTO.setCenterContactPerson("Mr. A. K. More");
		createExamCenterResponseDTO.setCenterContactPhone("9876543210");
		when(mockExamSchedulingService.createExamCenter(any(CreateExamCenterRequestDTO.class)))
			.thenReturn(createExamCenterResponseDTO);
		RequestBuilder request = MockMvcRequestBuilders.post("/api/v1/admin/centers")
				.contentType(MediaType.APPLICATION_JSON)
				.content(toJsonString(createExamCenterRequestDTO));
		MvcResult result = mockMvc.perform(request)
				.andDo(print())
				.andReturn();
		verify(mockExamSchedulingService, times(1)).createExamCenter(any(CreateExamCenterRequestDTO.class));
		assertEquals(HttpStatus.CREATED.value(), result.getResponse().getStatus());
		JsonNode jsonNode = toJsonNode(result.getResponse().getContentAsString());
		assertEquals(1L, jsonNode.get("centerId").asLong());
		assertEquals("A B College", jsonNode.get("centerName").asText());
		assertEquals("M.G.Road", jsonNode.get("centerAddress").asText());
		assertEquals("Pune", jsonNode.get("centerCity").asText());
		assertEquals("Maharashtra", jsonNode.get("centerState").asText());
		assertEquals("123456", jsonNode.get("centerPinCode").asText());
		assertEquals("Mr. A. K. More", jsonNode.get("centerContactPerson").asText());
		assertEquals("9876543210", jsonNode.get("centerContactPhone").asText());
	}

	@Test
	void test_createExamCenter_NameAlreadyExists_ThrowsException() throws Exception {
		CreateExamCenterRequestDTO createExamCenterRequestDTO = new CreateExamCenterRequestDTO();
		createExamCenterRequestDTO.setCenterName("A B College");
		createExamCenterRequestDTO.setCenterAddress("M.G.Road");
		createExamCenterRequestDTO.setCenterCity("Pune");
		createExamCenterRequestDTO.setCenterState("Maharashtra");
		createExamCenterRequestDTO.setCenterPinCode("123456");
		createExamCenterRequestDTO.setCenterContactPerson("Mr. A. K. More");
		createExamCenterRequestDTO.setCenterContactPhone("9876543210");

		when(mockExamSchedulingService.createExamCenter(any(CreateExamCenterRequestDTO.class)))
			.thenThrow(new IllegalArgumentException("An Exam Center with this Name already exists."));
		RequestBuilder request = MockMvcRequestBuilders.post("/api/v1/admin/centers")
				.contentType(MediaType.APPLICATION_JSON)
				.content(toJsonString(createExamCenterRequestDTO));
		MvcResult result = mockMvc.perform(request)
				.andDo(print())
				.andReturn();
		verify(mockExamSchedulingService, times(1)).createExamCenter(any(CreateExamCenterRequestDTO.class));
		assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
		JsonNode jsonNode = toJsonNode(result.getResponse().getContentAsString());
		assertTrue(jsonNode.get("error").asText().equals("An Exam Center with this Name already exists."));
	}

	@Test
	void test_addExamSlot_ExamCenterNotExists_RaiseExecption() throws Exception {
		doThrow(new IllegalArgumentException("Exam Center with id 1 not found."))
			.when(mockExamSchedulingService).addExamSlot(anyLong(), any(CreateExamSlotRequest.class));
		CreateExamSlotRequest slotRequest = CreateExamSlotRequest.builder()
				.examDate(LocalDate.of(2025, 10, 10))
				.examStartTime(LocalTime.of(10, 0))
				.examEndTime(LocalTime.of(11, 0))
				.totalSeats(25)
				.build();
		RequestBuilder request = MockMvcRequestBuilders.post("/api/v1/admin/centers/1/slots")
				.contentType(MediaType.APPLICATION_JSON)
				.content(toJsonString(slotRequest));
		MvcResult result = mockMvc.perform(request)
				.andDo(print())
				.andReturn();
		verify(mockExamSchedulingService).addExamSlot(anyLong(), any(CreateExamSlotRequest.class));
		assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
		JsonNode jsonNode = toJsonNode(result.getResponse().getContentAsString());
		assertEquals("Exam Center with id 1 not found.", jsonNode.get("error").asText());
	}

	@Test
	void test_addExamSlot_ExamCenterDateStartTimeEndTime_Exists_RaiseException() throws Exception {
		doThrow(new IllegalArgumentException("Exam Slot with centerId=1, date=2025-10-10, start time=10:00, end time=11:00 already exists."))
			.when(mockExamSchedulingService).addExamSlot(anyLong(), any(CreateExamSlotRequest.class));
		CreateExamSlotRequest slotRequest = CreateExamSlotRequest.builder()
				.examDate(LocalDate.of(2025, 10, 10))
				.examStartTime(LocalTime.of(10, 0))
				.examEndTime(LocalTime.of(11, 0))
				.totalSeats(25)
				.build();
		RequestBuilder request = MockMvcRequestBuilders.post("/api/v1/admin/centers/1/slots")
				.contentType(MediaType.APPLICATION_JSON)
				.content(toJsonString(slotRequest));
		MvcResult result = mockMvc.perform(request)
				.andDo(print())
				.andReturn();
		verify(mockExamSchedulingService).addExamSlot(anyLong(), any(CreateExamSlotRequest.class));
		assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
		JsonNode jsonNode = toJsonNode(result.getResponse().getContentAsString());
		assertEquals("Exam Slot with centerId=1, date=2025-10-10, start time=10:00, end time=11:00 already exists.",
				jsonNode.get("error").asText());
	}

	@Test
	void test_addExamSlot_New_ExamCenterDateStartTimeEndTime_Save() throws Exception {
		doNothing().when(mockExamSchedulingService).addExamSlot(anyLong(), any(CreateExamSlotRequest.class));
		CreateExamSlotRequest slotRequest = CreateExamSlotRequest.builder()
				.examDate(LocalDate.of(2025, 10, 10))
				.examStartTime(LocalTime.of(10, 0))
				.examEndTime(LocalTime.of(11, 0))
				.totalSeats(25)
				.build();
		RequestBuilder request = MockMvcRequestBuilders.post("/api/v1/admin/centers/1/slots")
				.contentType(MediaType.APPLICATION_JSON)
				.content(toJsonString(slotRequest));
		MvcResult result = mockMvc.perform(request)
				.andDo(print())
				.andReturn();
		verify(mockExamSchedulingService).addExamSlot(anyLong(), any(CreateExamSlotRequest.class));
		assertEquals(HttpStatus.CREATED.value(), result.getResponse().getStatus());
		JsonNode jsonNode = toJsonNode(result.getResponse().getContentAsString());
		assertEquals("Exam Slot added successfully",
				jsonNode.get("status").asText());
	}

	private String toJsonString(CreateExamCenterRequestDTO dto) throws JsonProcessingException {
		return objectMapper.writeValueAsString(dto);
	}

	private String toJsonString(CreateExamSlotRequest dto) throws JsonProcessingException {
		return objectMapper.writeValueAsString(dto);
	}

	private JsonNode toJsonNode(String jsonString) throws JsonMappingException, JsonProcessingException {
		return objectMapper.readTree(jsonString);
	}
}
