package com.example.offerdaysongs.controller;

import com.example.offerdaysongs.dto.ErrorResponse;
import com.example.offerdaysongs.dto.RecordingDto;
import com.example.offerdaysongs.dto.SingerDto;
import com.example.offerdaysongs.dto.requests.CreateRecordingRequest;
import com.example.offerdaysongs.model.Recording;
import com.example.offerdaysongs.service.CopyrightService;
import com.example.offerdaysongs.service.RecordingService;
import com.example.offerdaysongs.service.exception.CompanyNotFoundException;
import com.example.offerdaysongs.service.exception.CopyrightNotFoundException;
import com.example.offerdaysongs.service.exception.RecordingNotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/recordings")
public class RecordingController {
    private static final String ID = "id";
    private final RecordingService recordingService;
    private final CopyrightService copyrightService;
    private final ObjectMapper mapper;

    public RecordingController(RecordingService recordingService, CopyrightService copyrightService, ObjectMapper mapper) {
        this.recordingService = recordingService;
        this.copyrightService = copyrightService;
        this.mapper = mapper;
    }

    @GetMapping("/")
    public List<RecordingDto> getAll() {
        return recordingService.getAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id:[\\d]+}")
    public RecordingDto get(@PathVariable(ID) long id) {
        var recording = recordingService.getById(id);
        return convertToDto(recording);
    }

    @PostMapping("/")
    public RecordingDto create(@RequestBody CreateRecordingRequest request) {
        return convertToDto(recordingService.create(request));
    }

    @GetMapping("/{id:[\\d]+}/copyrights")
    public ResponseEntity<String> getRecordingTotalRoyalty(@PathVariable long id,
                                                           @RequestParam("countTotalRoyalty") boolean countTotalRoyalty) {
        if (countTotalRoyalty) {
            final var totalRoyalty = Map.of("totalRoyalty", copyrightService.getTotalRoyalty(id));
            try {
                return ResponseEntity.ok(mapper.writeValueAsString(totalRoyalty));
            } catch (JsonProcessingException e) {
                return ResponseEntity.internalServerError().build();
            }
        } else {
            return ResponseEntity.badRequest().build();
        }
    }


    private RecordingDto convertToDto(Recording recording) {
        var singer = recording.getSinger();
        return new RecordingDto(recording.getId(),
                recording.getTitle(),
                recording.getVersion(),
                recording.getReleaseTime(),
                singer != null ? new SingerDto(singer.getId(), singer.getName()) : null);


    }
    @ExceptionHandler({RecordingNotFoundException.class})
    public ResponseEntity<ErrorResponse> handleNotFound() {
        final var timestamp = ZonedDateTime.of(LocalDateTime.now(), ZoneId.systemDefault());
        return new ResponseEntity<>(new ErrorResponse(timestamp, HttpStatus.BAD_REQUEST.value(), "Resource not found"), HttpStatus.BAD_REQUEST);
    }
}
