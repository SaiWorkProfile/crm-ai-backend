package com.realestate.ai.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.realestate.ai.model.LoginLog;

public interface LoginLogRepository
extends JpaRepository<LoginLog,Long>{

List<LoginLog> findAllByOrderByTimeDesc();

List<LoginLog> findByAdminIdOrderByTimeDesc(Long adminId);
}
