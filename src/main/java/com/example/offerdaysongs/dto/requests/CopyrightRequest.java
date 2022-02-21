package com.example.offerdaysongs.dto.requests;

import com.example.offerdaysongs.model.Company;
import com.example.offerdaysongs.model.Recording;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class CopyrightRequest {
    Double royalty;
    ZonedDateTime startTime;
    ZonedDateTime expiryTime;
    Company company;
    Recording recording;
}
