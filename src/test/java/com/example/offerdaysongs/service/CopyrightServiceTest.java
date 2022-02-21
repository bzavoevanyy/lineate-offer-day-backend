package com.example.offerdaysongs.service;

import com.example.offerdaysongs.dto.requests.CopyrightRequest;
import com.example.offerdaysongs.model.Company;
import com.example.offerdaysongs.model.Copyright;
import com.example.offerdaysongs.model.Recording;
import com.example.offerdaysongs.model.Singer;
import com.example.offerdaysongs.repository.CompanyRepository;
import com.example.offerdaysongs.repository.CopyrightRepository;
import com.example.offerdaysongs.repository.RecordingRepository;
import com.example.offerdaysongs.service.exception.CopyrightNotFoundException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CopyrightService test")
class CopyrightServiceTest {

    private final CopyrightRepository copyrightRepository = mock(CopyrightRepository.class);

    private final CompanyRepository companyRepository = mock(CompanyRepository.class);

    private final RecordingRepository recordingRepository = mock(RecordingRepository.class);

    private final CopyrightService copyrightService = new CopyrightService(copyrightRepository, companyRepository, recordingRepository);

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
    @DisplayName(" getById should return Copyright entity")
    void getById_should_return_copyright_entity() {
        when(copyrightRepository.findById(anyLong())).thenReturn(Optional.of(COPYRIGHT));
        final var copyright = copyrightService.getById(1L);
        assertThat(copyright).usingRecursiveComparison().isEqualTo(COPYRIGHT);
        verify(copyrightRepository, times(1)).findById(anyLong());
    }

    @Test
    @DisplayName(" getById should throw CopyrightNotFoundException")
    void getById_should_throw_exception() {
        when(copyrightRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> copyrightService.getById(1L)).isInstanceOf(CopyrightNotFoundException.class);
    }

    @Test
    @DisplayName(" getByPeriod should return List of entities")
    void getByPeriod_should_return_list_of_entities() {
        when(copyrightRepository.findAllByStartTimeAfterAndExpiryTimeBefore(any(), any())).thenReturn(List.of(COPYRIGHT));
        assertThat(copyrightService.getAllByPeriod(COMMON_ZONED_DATE_TIME, COMMON_ZONED_DATE_TIME))
                .isNotNull().hasSize(1).first().usingRecursiveComparison().isEqualTo(COPYRIGHT);
    }

    @Test
    @DisplayName(" create should create entity and return it")
    void create_should_create_entity_and_return() {
        when(copyrightRepository.save(any())).thenReturn(COPYRIGHT);
        when(companyRepository.findById(anyLong())).thenReturn(Optional.of(COMPANY));
        when(recordingRepository.findById(anyLong())).thenReturn(Optional.of(RECORDING));
        assertThat(copyrightService.create(POST_REQUEST)).usingRecursiveComparison().isEqualTo(COPYRIGHT);
    }

    @Test
    @DisplayName(" update should patch entity")
    void update_should_patch_entity() {
        when(copyrightRepository.findById(anyLong())).thenReturn(Optional.of(COPYRIGHT));
        copyrightService.update(1L, PATCH_REQUEST);
        assertThat(COPYRIGHT.getRoyalty()).isEqualTo(1.0);
        verify(copyrightRepository, times(1)).save(any());
    }

    @Test
    @DisplayName(" delete should delete entity")
    void should_delete_entity_entity() {
        when(copyrightRepository.existsById(anyLong())).thenReturn(true);
        copyrightService.delete(1L);
        verify(copyrightRepository, times(1)).deleteById(anyLong());
    }

    @Test
    @DisplayName(" getAllByCompany should return List of entities")
    void getAllByCompany_should_list_of_entities() {
        when(copyrightRepository.findCopyrightsByCompany(any())).thenReturn(List.of(COPYRIGHT));
        assertThat(copyrightService.getAllByCompany(COMPANY)).isNotNull().hasSize(1).first().usingRecursiveComparison()
                .isEqualTo(COPYRIGHT);
    }
}