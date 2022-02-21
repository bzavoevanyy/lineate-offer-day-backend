package com.example.offerdaysongs.service;

import com.example.offerdaysongs.dto.requests.CopyrightRequest;
import com.example.offerdaysongs.model.Company;
import com.example.offerdaysongs.model.Copyright;
import com.example.offerdaysongs.repository.CompanyRepository;
import com.example.offerdaysongs.repository.CopyrightRepository;
import com.example.offerdaysongs.repository.RecordingRepository;
import com.example.offerdaysongs.service.exception.CompanyNotFoundException;
import com.example.offerdaysongs.service.exception.CopyrightNotFoundException;
import com.example.offerdaysongs.service.exception.RecordingNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.ZonedDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CopyrightService {
    private final CopyrightRepository copyrightRepository;
    private final CompanyRepository companyRepository;
    private final RecordingRepository recordingRepository;

    public Copyright getById(Long id) {
        return copyrightRepository.findById(id).orElseThrow(CopyrightNotFoundException::new);
    }

    public List<Copyright> getAllByPeriod(ZonedDateTime startTime, ZonedDateTime expireTime) {
        return copyrightRepository.findAllByStartTimeAfterAndExpiryTimeBefore(startTime, expireTime);
    }

    @Transactional
    public Copyright create(CopyrightRequest request) {
        var company = companyRepository.findById(request.getCompany().getId()).orElseThrow(CompanyNotFoundException::new);
        var recording = recordingRepository.findById(request.getRecording().getId()).orElseThrow(RecordingNotFoundException::new);
        var copyright = new Copyright();
        copyright.setRoyalty(request.getRoyalty());
        copyright.setStartTime(request.getStartTime());
        copyright.setExpiryTime(request.getExpiryTime());
        copyright.setCompany(company);
        copyright.setRecording(recording);
        return copyrightRepository.save(copyright);
    }

    @Transactional
    public void update(long id, CopyrightRequest request) {
        final var copyright = copyrightRepository.findById(id).orElseThrow(CopyrightNotFoundException::new);
        if (request.getRoyalty() != null) {
            copyright.setRoyalty(request.getRoyalty());
        }
        if (request.getExpiryTime() != null) {
            copyright.setExpiryTime(request.getExpiryTime());
        }
        if (request.getCompany() != null) {
            copyright.setCompany(request.getCompany());
        }
        copyrightRepository.save(copyright);
    }

    @Transactional
    public void delete(Long id) {
        if (copyrightRepository.existsById(id)) {
            copyrightRepository.deleteById(id);
        } else {
            throw new CopyrightNotFoundException();
        }
    }

    public List<Copyright> getAllByCompany(Company company) {
        return copyrightRepository.findCopyrightsByCompany(company);
    }

    public Double getTotalRoyalty(Long recordingId) {
        final var recording = recordingRepository.findById(recordingId).orElseThrow(RecordingNotFoundException::new);
        final var copyrights = copyrightRepository.findAllByRecording(recording);
        return copyrights.stream().map(Copyright::getRoyalty).reduce(Double::sum).orElse(0.0);
    }
}
