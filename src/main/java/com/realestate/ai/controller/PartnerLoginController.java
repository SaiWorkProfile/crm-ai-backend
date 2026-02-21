package com.realestate.ai.controller;

import com.realestate.ai.model.ClientRole;
import com.realestate.ai.model.ClientUser;
import com.realestate.ai.model.Partner;
import com.realestate.ai.repository.ClientUserRepository;
import com.realestate.ai.repository.PartnerRepository;
import com.realestate.ai.security.JwtUtil;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/partner")
public class PartnerLoginController {

private final ClientUserRepository clientRepo;
private final PartnerRepository partnerRepo;
private final JwtUtil jwtUtil;
private final PasswordEncoder encoder;

public PartnerLoginController(
ClientUserRepository clientRepo,
PartnerRepository partnerRepo,
JwtUtil jwtUtil,
PasswordEncoder encoder){

this.clientRepo = clientRepo;
this.partnerRepo = partnerRepo;
this.jwtUtil = jwtUtil;
this.encoder = encoder;
}

@PostMapping("/login")
public Map<String,String> login(
@RequestBody Map<String,String> body){

System.out.println("🚀 PARTNER LOGIN HIT");

String email = body.get("email");
String password = body.get("password");

ClientUser user =
clientRepo.findByEmail(email)
.orElseThrow(() ->
new RuntimeException("Partner Not Found"));


// ❌ WRONG LOGIN TYPE
if(user.getRole()!=ClientRole.PARTNER){
throw new RuntimeException(
"This account is not a Partner");
}


// ❌ EMAIL NOT VERIFIED
if(!user.getActive()){
throw new RuntimeException(
"Activate your account via email");
}


// ❌ PASSWORD NOT SET / WRONG
if(user.getPassword()==null ||
!encoder.matches(password,user.getPassword())){

throw new RuntimeException(
"Invalid Password");
}


// ❌ ADMIN NOT APPROVED
Partner partner =
partnerRepo.findByEmail(email)
.orElseThrow();

if(!partner.getActive()){
throw new RuntimeException(
"Partner Not Approved by Admin");
}


// ❌ KYC NOT VERIFIED
if(!partner.getKycVerified()){
throw new RuntimeException(
"KYC not verified");
}


// ✅ LOGIN SUCCESS
String jwt =
jwtUtil.generateToken(
user.getEmail(),
"PARTNER"
);

System.out.println("✅ PARTNER LOGIN SUCCESS");

return Map.of(
"token", jwt,
"role", "PARTNER"
);
}
}