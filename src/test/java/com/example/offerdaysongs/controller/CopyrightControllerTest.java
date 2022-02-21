package com.example.offerdaysongs.controller;

import com.example.offerdaysongs.dto.CopyrightDto;
import com.example.offerdaysongs.dto.requests.CopyrightRequest;
import com.example.offerdaysongs.model.Company;
import com.example.offerdaysongs.model.Copyright;
import com.example.offerdaysongs.model.Recording;
import com.example.offerdaysongs.model.Singer;
import com.example.offerdaysongs.service.CopyrightService;
import com.example.offerdaysongs.service.exception.CopyrightNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.val;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CopyrightController.class)
@DisplayName("CopyrightController test")
class CopyrightControllerTest {
    @MockBean
    private CopyrightService copyrightService;
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper mapper;

    private final static Company COMPANY = new Company();
    private final static Recording RECORDING = new Recording();
    private final static Singer SINGER = new Singer();
    private final static Copyright COPYRIGHT = new Copyright();
    private final static ZonedDateTime COMMON_ZONED_DATE_TIME = ZonedDateTime.of(2000, 1, 1, 0, 0, 0, 0, ZoneId.systemDefault());
    private final static CopyrightRequest POST_REQUEST = new CopyrightRequest();
    private final static CopyrightRequest PATCH_REQUEST = new CopyrightRequest();

    @BeforeAll
    static void beforeAll() {
        COMPANY.setId(1L);
        COMPANY.setName("test company");

        SINGER.setId(1L);
        SINGER.setName("test singer");

        RECORDING.setId(1L);
        RECORDING.setTitle("test recording");
        RECORDING.setReleaseTime(COMMON_ZONED_DATE_TIME);
        RECORDING.setSinger(SINGER);
        RECORDING.setVersion("1");

        COPYRIGHT.setId(1L);
        COPYRIGHT.setRoyalty(0.0);
        COPYRIGHT.setExpiryTime(COMMON_ZONED_DATE_TIME);
        COPYRIGHT.setStartTime(COMMON_ZONED_DATE_TIME);
        COPYRIGHT.setRecording(RECORDING);
        COPYRIGHT.setCompany(COMPANY);

        POST_REQUEST.setRoyalty(0.0);
        POST_REQUEST.setStartTime(COMMON_ZONED_DATE_TIME);
        POST_REQUEST.setExpiryTime(COMMON_ZONED_DATE_TIME);
        POST_REQUEST.setCompany(COMPANY);
        POST_REQUEST.setRecording(RECORDING);

        PATCH_REQUEST.setRoyalty(1.0);
    }

    @Test
    @DisplayName(" should return json with copyright")
    void should_return_json_with_copyright() throws Exception {
        given(copyrightService.getById(anyLong())).willReturn(COPYRIGHT);
        val expectedResult = CopyrightDto.toDto(COPYRIGHT);
        mvc.perform(get("/api/copyrights/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(expectedResult)));
    }

    @Test
    @DisplayName(" should return json with error message")
    void should_return_json_with_error_message() throws Exception {
        given(copyrightService.getById(anyLong())).willThrow(CopyrightNotFoundException.class);
        mvc.perform(get("/api/copyrights/1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName(" should return json with list copyrights by period")
    void should_return_json_with_list_copyrights_by_period() throws Exception {
        given(copyrightService.getAllByPeriod(any(), any())).willReturn(List.of(COPYRIGHT));
        val expectedResult = Stream.of(COPYRIGHT).map(CopyrightDto::toDto).collect(Collectors.toList());
        mvc.perform(get("/api/copyrights/")
                        .param("startTime", COMMON_ZONED_DATE_TIME.toString())
                        .param("expiryTime", COMMON_ZONED_DATE_TIME.toString()))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(expectedResult)));
    }

    @Test
    @DisplayName(" should return json with list copyrights by period")
    void should_create_copyright_and_return_correct_json() throws Exception {
        given(copyrightService.create(any())).willReturn(COPYRIGHT);
        val expectedResult = CopyrightDto.toDto(COPYRIGHT);
        mvc.perform(post("/api/copyrights/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(POST_REQUEST)))
                .andExpect(status().isCreated())
                .andExpect(content().json(mapper.writeValueAsString(expectedResult)));
    }

    @Test
    @DisplayName(" should invoke update method with right args")
    void should_invoke_update_method_with_right_args() throws Exception {
        doNothing().when(copyrightService).update(anyLong(), any());
        mvc.perform(patch("/api/copyrights/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(PATCH_REQUEST)))
                .andExpect(status().isNoContent());
        verify(copyrightService, times(1)).update(1L, PATCH_REQUEST);
    }

    @Test
    @DisplayName(" should invoke delete method with right args")
    void should_invoke_delete_method_with_right_args() throws Exception {
        doNothing().when(copyrightService).delete(anyLong());
        mvc.perform(delete("/api/copyrights/1"))
                .andExpect(status().isNoContent());
        verify(copyrightService, times(1)).delete(1L);
    }

}