package com.realestate.ai.model;

import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name="login_logs")
public class LoginLog {

@Id
@GeneratedValue(strategy=GenerationType.IDENTITY)
private Long id;

private Long adminId;
private String ip;

// 🔥 NEW
private String deviceType;
private String os;
private String browser;

private String action;
private LocalDateTime time;

@PrePersist
void onCreate(){
time = LocalDateTime.now();
}

// getters setters

public Long getId(){return id;}
public void setId(Long id){this.id=id;}

public Long getAdminId(){return adminId;}
public void setAdminId(Long adminId){this.adminId=adminId;}

public String getIp(){return ip;}
public void setIp(String ip){this.ip=ip;}

public String getDeviceType(){return deviceType;}
public void setDeviceType(String deviceType){this.deviceType=deviceType;}

public String getOs(){return os;}
public void setOs(String os){this.os=os;}

public String getBrowser(){return browser;}
public void setBrowser(String browser){this.browser=browser;}

public String getAction(){return action;}
public void setAction(String action){this.action=action;}

public LocalDateTime getTime(){return time;}
}
