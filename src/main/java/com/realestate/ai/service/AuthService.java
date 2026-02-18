package com.realestate.ai.service;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.realestate.ai.model.Admin;
import com.realestate.ai.model.PasswordResetOtp;
import com.realestate.ai.model.LoginLog;
import com.realestate.ai.repository.AdminRepository;
import com.realestate.ai.repository.PasswordResetOtpRepository;
import com.realestate.ai.repository.LoginLogRepository;
import com.realestate.ai.util.DeviceParser;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class AuthService {

private final AdminRepository adminRepo;
private final PasswordResetOtpRepository otpRepo;
private final LoginLogRepository loginLogRepo;
private final EmailService emailService;
private final PasswordEncoder encoder;

public AuthService(AdminRepository adminRepo,
PasswordResetOtpRepository otpRepo,
LoginLogRepository loginLogRepo,
EmailService emailService,
PasswordEncoder encoder){

this.adminRepo=adminRepo;
this.otpRepo=otpRepo;
this.loginLogRepo=loginLogRepo;
this.emailService=emailService;
this.encoder=encoder;
}

// ================= LOGIN =================
public Admin login(String email,String password,String ip,String device){

Admin admin=adminRepo.findByEmail(email)
.orElseThrow(()->new RuntimeException("Admin not found"));

if(!admin.isActive())
throw new RuntimeException("Account disabled");

if(!encoder.matches(password,admin.getPassword()))
throw new RuntimeException("Invalid password");

// session token
admin.setRefreshToken(UUID.randomUUID().toString());
admin.setLastLoginAt(LocalDateTime.now());
admin.setLastLoginIp(ip);
admin.setLastLoginDevice(device);

adminRepo.save(admin);

log(admin.getId(),ip,device,"LOGIN");

return admin;
}

// ================= LOGOUT =================
public void logout(Admin admin,String ip,String device){

if(admin==null){
System.out.println("⚠️ Logout skipped - admin null (Voice AI call)");
return;
}

admin.setRefreshToken(null);
adminRepo.save(admin);

log(admin.getId(),ip,device,"LOGOUT");
}

// ============ LOGOUT ALL DEVICES ============
public void logoutAll(Admin admin,String ip,String device){

if(admin==null){
System.out.println("⚠️ LogoutAll skipped - admin null");
return;
}

admin.setRefreshToken(null);
adminRepo.save(admin);

log(admin.getId(),ip,device,"LOGOUT_ALL");
}

// =========== FORGOT PASSWORD ===========
public void sendResetOtp(String email){

adminRepo.findByEmail(email)
.orElseThrow(()->new RuntimeException("Admin not found"));

otpRepo.deleteByEmail(email);

String otp=
String.valueOf(100000+
new Random().nextInt(900000));

PasswordResetOtp entity=new PasswordResetOtp();
entity.setEmail(email);
entity.setOtp(otp);
entity.setExpiry(LocalDateTime.now().plusMinutes(5));

otpRepo.save(entity);
emailService.sendOtp(email,otp);
}

// ===== RESET PASSWORD =====
public void resetPassword(String email,
String otp,
String newPassword,
String ip,
String device){

PasswordResetOtp entity=
otpRepo.findByEmailAndOtp(email,otp)
.orElseThrow(()->new RuntimeException("Invalid OTP"));

if(entity.getExpiry()
.isBefore(LocalDateTime.now()))
throw new RuntimeException("OTP expired");

Admin admin=adminRepo.findByEmail(email)
.orElseThrow(()->new RuntimeException("Admin not found"));

admin.setPassword(encoder.encode(newPassword));
admin.setRefreshToken(null);

adminRepo.save(admin);
otpRepo.deleteByEmail(email);

log(admin.getId(),ip,device,"PASSWORD_RESET");
}

// ================= LOGGER =================
private void log(Long adminId,
String ip,
String ua,
String action){

LoginLog log=new LoginLog();

log.setAdminId(adminId);
log.setIp(ip);

log.setDeviceType(
DeviceParser.getDevice(ua)
);

log.setOs(
DeviceParser.getOS(ua)
);

log.setBrowser(
DeviceParser.getBrowser(ua)
);

log.setAction(action);

loginLogRepo.save(log);
}
}
