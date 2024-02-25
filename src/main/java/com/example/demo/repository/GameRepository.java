package com.example.demo.repository;

import com.example.demo.domain.entity.GameInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GameRepository extends JpaRepository<GameInfo, Long> {
    List<GameInfo> findByIsEndedFalse();
}
