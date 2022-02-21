package com.example.offerdaysongs.dto;

import com.example.offerdaysongs.model.Copyright;
import lombok.Builder;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
@Builder
public class CopyrightDto {
    Long id;
    Double royalty;
    ZonedDateTime startTime;
    ZonedDateTime expiryTime;
    CompanyDto company;
    RecordingDto recording;

    public static CopyrightDto toDto(Copyright copyright) {
        var company = copyright.getCompany();
        var recording = copyright.getRecording();
        var singer = recording.getSinger();
        return CopyrightDto.builder()
                .id(copyright.getId())
                .royalty(copyright.getRoyalty())
                .startTime(copyright.getStartTime())
                .expiryTime(copyright.getExpiryTime())
                .company(
                        new CompanyDto(company.getId(), company.getName()))
                .recording(
                        new RecordingDto(recording.getId(),
                                recording.getTitle(),
                                recording.getVersion(),
                                recording.getReleaseTime(),
                                new SingerDto(singer.getId(), singer.getName())))
                .build();
    }
}
