package com.example.offerdaysongs.controller;

import com.example.offerdaysongs.dto.CopyrightDto;
import com.example.offerdaysongs.dto.ErrorResponse;
import com.example.offerdaysongs.dto.requests.CopyrightRequest;
import com.example.offerdaysongs.service.CopyrightService;
import com.example.offerdaysongs.service.exception.CompanyNotFoundException;
import com.example.offerdaysongs.service.exception.CopyrightNotFoundException;
import com.example.offerdaysongs.service.exception.RecordingNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/copyrights")
@RequiredArgsConstructor
public class CopyrightController {
    private static final String ID = "id";
    private static final String START_TIME = "startTime";
    private static final String EXPIRY_TIME = "expiryTime";

    private final CopyrightService copyrightService;

    @GetMapping("/{id:[\\d]+}")
    public CopyrightDto getById(@PathVariable(ID) long id) {
        return CopyrightDto.toDto(copyrightService.getById(id));
    }

    // TODO The term of reference should be refined
    @GetMapping("/")
    public List<CopyrightDto> getByPeriod(@RequestParam(START_TIME) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                                  ZonedDateTime startDateTime,
                                          @RequestParam(EXPIRY_TIME) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                                  ZonedDateTime expiryDateTime) {
        final var copyrights = copyrightService.getAllByPeriod(startDateTime, expiryDateTime);
        return copyrights.stream().map(CopyrightDto::toDto).collect(Collectors.toList());
    }

    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    public CopyrightDto create(@RequestBody CopyrightRequest request) {
        return CopyrightDto.toDto(copyrightService.create(request));
    }

    // TODO Make better solution with json-patch (RFC6902) or json-patch-merge (RFC5741)
    @PatchMapping(value = "/{id:[\\d]+}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable(ID) long id, @RequestBody CopyrightRequest request) {
        copyrightService.update(id, request);
    }

    @DeleteMapping("/{id:[\\d]+}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable(ID) long id) {
        copyrightService.delete(id);
    }

    @ExceptionHandler({CopyrightNotFoundException.class, CompanyNotFoundException.class, RecordingNotFoundException.class})
    public ResponseEntity<ErrorResponse> handleNotFound() {
        final var timestamp = ZonedDateTime.of(LocalDateTime.now(), ZoneId.systemDefault());
        return new ResponseEntity<>(new ErrorResponse(timestamp, HttpStatus.BAD_REQUEST.value(), "Resource not found"), HttpStatus.BAD_REQUEST);
    }
}
