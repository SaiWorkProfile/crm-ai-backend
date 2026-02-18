package com.realestate.ai.dto;

import java.time.LocalDateTime;

public class LoginLogDto {

private Long adminId;
private String email;
private String role;
private String ip;
private String deviceType;
private String os;
private String browser;
private String action;
private LocalDateTime time;

public LoginLogDto(Long adminId,
String email,
String role,
String ip,
String deviceType,
String os,
String browser,
String action,
LocalDateTime time){

this.adminId=adminId;
this.email=email;
this.role=role;
this.ip=ip;
this.deviceType=deviceType;
this.os=os;
this.browser=browser;
this.action=action;
this.time=time;
}

// getters
public Long getAdminId(){return adminId;}
public String getEmail(){return email;}
public String getRole(){return role;}
public String getIp(){return ip;}
public String getDeviceType(){return deviceType;}
public String getOs(){return os;}
public String getBrowser(){return browser;}
public String getAction(){return action;}
public LocalDateTime getTime(){return time;}
}
