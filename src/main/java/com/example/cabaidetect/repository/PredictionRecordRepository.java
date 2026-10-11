package com.example.cabaidetect.repository;

import com.example.cabaidetect.model.PredictionRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PredictionRecordRepository extends JpaRepository<PredictionRecord, Long> {
    List<PredictionRecord> findByUserIdOrderByTanggalDeteksiDesc(Long userId);
}
