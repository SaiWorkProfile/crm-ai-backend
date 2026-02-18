package com.realestate.ai.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.realestate.ai.dto.LoginLogDto;
import com.realestate.ai.model.Admin;
import com.realestate.ai.model.LoginLog;
import com.realestate.ai.repository.AdminRepository;
import com.realestate.ai.repository.LoginLogRepository;

@Service
public class LoginLogService {

private final LoginLogRepository repo;
private final AdminRepository adminRepo;

public LoginLogService(
LoginLogRepository repo,
AdminRepository adminRepo){

this.repo=repo;
this.adminRepo=adminRepo;
}

// 🔥 ALL ROLES LOGIN HISTORY
public List<LoginLogDto> getAllLogs(){

return repo.findAllByOrderByTimeDesc()
.stream()
.map(log->{

Admin admin =
adminRepo.findById(
log.getAdminId()
).orElse(null);

return new LoginLogDto(
log.getAdminId(),
admin!=null?admin.getEmail():"Deleted",
admin!=null?admin.getRole().name():"NA",
log.getIp(),
log.getDeviceType(),
log.getOs(),
log.getBrowser(),
log.getAction(),
log.getTime()
);

}).collect(Collectors.toList());
}
}
