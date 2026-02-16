package com.realestate.ai.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.realestate.ai.model.LoginLog;

public interface LoginLogRepository extends JpaRepository<LoginLog, Long> {
}
