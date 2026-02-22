package com.realestate.ai.controller;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.realestate.ai.model.ClientUser;
import com.realestate.ai.model.Partner;
import com.realestate.ai.model.PartnerActivationToken;
import com.realestate.ai.repository.ClientUserRepository;
import com.realestate.ai.repository.PartnerActivationTokenRepository;
import com.realestate.ai.repository.PartnerRepository;

@RestController

@RequestMapping("/api/auth/partner")
public class PartnerActivationController {

private final PartnerActivationTokenRepository tokenRepo;
private final PartnerRepository partnerRepo;
private final ClientUserRepository clientRepo;
private final PasswordEncoder encoder;

public PartnerActivationController(
PartnerActivationTokenRepository tokenRepo,
PartnerRepository partnerRepo,
ClientUserRepository clientRepo,
PasswordEncoder encoder){

this.tokenRepo = tokenRepo;
this.partnerRepo = partnerRepo;
this.clientRepo = clientRepo;
this.encoder = encoder;
}

@PostMapping("/set-password")
public String setPassword(
@RequestBody Map<String,String> body){

String token = body.get("token");
String password = body.get("password");

PartnerActivationToken t =
tokenRepo.findByToken(token)
.orElseThrow(() ->
new RuntimeException("Invalid Token"));

if(t.getExpiry().isBefore(LocalDateTime.now())){
throw new RuntimeException("Token Expired");
}

Partner partner =
partnerRepo.findById(t.getPartnerId())
.orElseThrow();

ClientUser user =
clientRepo.findFirstByEmail(partner.getEmail())
.orElseThrow();

user.setPassword(encoder.encode(password));
user.setActive(true);

clientRepo.save(user);

tokenRepo.delete(t);

return "Partner Activated!";
}
}