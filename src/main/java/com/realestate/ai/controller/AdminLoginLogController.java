package com.realestate.ai.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.realestate.ai.dto.LoginLogDto;
import com.realestate.ai.service.LoginLogService;

@RestController
@RequestMapping("/api/admin/login-logs")
@CrossOrigin
public class AdminLoginLogController {

private final LoginLogService service;

public AdminLoginLogController(
LoginLogService service){

this.service=service;
}

// 🔥 ADMIN PANEL API
@GetMapping
public List<LoginLogDto> logs(){
return service.getAllLogs();
}
}
