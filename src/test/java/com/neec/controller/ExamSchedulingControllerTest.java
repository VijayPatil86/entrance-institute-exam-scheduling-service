package com.neec.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
import java.util.List;

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
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neec.dto.CreateExamCenterRequestDTO;
import com.neec.dto.CreateExamCenterResponseDTO;
import com.neec.dto.CreateExamSlotRequest;
import com.neec.dto.ExamCenterResponseDTO;
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

	@Test
	void test_getAllExamCenters() throws Exception {
		List<ExamCenterResponseDTO> listExamCenterResponseDTOs = List.of(
				buildAndReturn_ExamCenterResponseDTO(1L, "College of Commerce", "M.G.Road", "Thane", "Maharashtra", "123456", "Mr. More", "9876543210"),
				buildAndReturn_ExamCenterResponseDTO(2L, "College of Arts", "C.D. Road", "Delhi", "Delhi", "456789", "Mr. Sane", "8876543210")
		);
		when(mockExamSchedulingService.getAllCenters()).thenReturn(listExamCenterResponseDTOs);
		RequestBuilder request = MockMvcRequestBuilders.get("/api/v1/admin/centers");
		MvcResult result = mockMvc.perform(request)
				.andDo(print())
				.andReturn();
		assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
		assertNotNull(result.getResponse().getContentAsString());
		List<ExamCenterResponseDTO> returnedListExamCenterResponseDTOs = objectMapper.readValue(
				result.getResponse().getContentAsString(),
				new TypeReference<List<ExamCenterResponseDTO>>() {}
			);
		ExamCenterResponseDTO dto = returnedListExamCenterResponseDTOs.get(0);
		assertEquals(1L, dto.getCenterId());
		assertEquals("College of Commerce", dto.getCenterName());
		assertEquals("M.G.Road", dto.getCenterAddress());
		assertEquals("Thane", dto.getCenterCity());
		assertEquals("Maharashtra", dto.getCenterState());
		assertEquals("123456", dto.getCenterPinCode());
		assertEquals("Mr. More", dto.getCenterContactPerson());
		assertEquals("9876543210", dto.getCenterContactPhone());
		assertNotNull(returnedListExamCenterResponseDTOs.get(1));
		dto = returnedListExamCenterResponseDTOs.get(1);
		assertEquals(2L, dto.getCenterId());
		assertEquals("College of Arts", dto.getCenterName());
		assertEquals("C.D. Road", dto.getCenterAddress());
		assertEquals("Delhi", dto.getCenterCity());
		assertEquals("Delhi", dto.getCenterState());
		assertEquals("456789", dto.getCenterPinCode());
		assertEquals("Mr. Sane", dto.getCenterContactPerson());
		assertEquals("8876543210", dto.getCenterContactPhone());
	}

	private ExamCenterResponseDTO buildAndReturn_ExamCenterResponseDTO(Long centerId, String centerName,
			String centerAddress, String centerCity, String centerState,
			String centerPinCode, String centerContactPerson, String centerContactPhone) {
		return ExamCenterResponseDTO.builder()
				.centerId(centerId)
				.centerName(centerName)
				.centerAddress(centerAddress)
				.centerCity(centerCity)
				.centerState(centerState)
				.centerPinCode(centerPinCode)
				.centerContactPerson(centerContactPerson)
				.centerContactPhone(centerContactPhone)
				.build();
	}

	@Test
	void test_getAvailableCities_DistinctCityNames_NaturalOrder() throws Exception {
		List<String> listCityNames = List.of("Mumbai", "Pune", "Thane");
		when(mockExamSchedulingService.getAvailableCities()).thenReturn(listCityNames);
		RequestBuilder request = MockMvcRequestBuilders.get("/api/v1/centers/cities");
		MvcResult result = mockMvc.perform(request)
				.andDo(print())
				.andReturn();
		verify(mockExamSchedulingService).getAvailableCities();
		assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
		List<String> returnedListCityNames = objectMapper.readValue(
				result.getResponse().getContentAsString(),
				new TypeReference<List<String>>() {});
		assertEquals(listCityNames, returnedListCityNames, "Returned city list is incorrect — content or order does not match expected");
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
