package com.example.demo.repository;

import com.example.demo.domain.entity.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserInfo, Long> {
    UserInfo findByUserId(String userId);
}
