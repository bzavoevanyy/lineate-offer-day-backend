package com.example.offerdaysongs.repository;

import com.example.offerdaysongs.model.Company;
import com.example.offerdaysongs.model.Copyright;
import com.example.offerdaysongs.model.Recording;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;


public interface CopyrightRepository extends JpaRepository<Copyright, Long> {

    @EntityGraph("copyright-entity-graph-with-company-recording")
    Optional<Copyright> findById(Long id);

    @EntityGraph("copyright-entity-graph-with-company-recording")
    List<Copyright> findCopyrightsByCompany(Company company);

    @EntityGraph("copyright-entity-graph-with-company-recording")
    List<Copyright> findAllByStartTimeAfterAndExpiryTimeBefore(ZonedDateTime startTime, ZonedDateTime expiryTime);

    void deleteById(Long id);

    @EntityGraph("copyright-entity-graph-with-company-recording")
    List<Copyright> findAllByRecording(Recording recording);
}
