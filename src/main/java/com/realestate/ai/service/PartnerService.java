package com.realestate.ai.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.realestate.ai.model.ClientRole;
import com.realestate.ai.model.ClientUser;
import com.realestate.ai.model.Partner;
import com.realestate.ai.model.PartnerActivationToken;
import com.realestate.ai.model.PartnerType;
import com.realestate.ai.repository.ClientUserRepository;
import com.realestate.ai.repository.PartnerActivationTokenRepository;
import com.realestate.ai.repository.PartnerRepository;

@Service
public class PartnerService {

private final PartnerRepository repo;
private final ClientUserRepository clientRepo;
private final PartnerActivationTokenRepository tokenRepo;
private final EmailService emailService;

public PartnerService(
PartnerRepository repo,
ClientUserRepository clientRepo,
PartnerActivationTokenRepository tokenRepo,
EmailService emailService){

this.repo=repo;
this.clientRepo=clientRepo;
this.tokenRepo=tokenRepo;
this.emailService=emailService;
}

// ONBOARD PARTNER
public Partner onboard(Partner p){

if(p.getPartnerType()==PartnerType.LEGACY_PARTNER){

repo.findByPincodeAndDeletedFalse(
p.getPincode())
.ifPresent(x->{
throw new RuntimeException(
"Legacy Partner exists for this pincode");
});
}

Partner saved=repo.save(p);

// CREATE LOGIN ENTRY
ClientUser user=new ClientUser();
user.setName(saved.getName());
user.setEmail(saved.getEmail());
user.setPhone(saved.getPhone());
user.setRole(ClientRole.PARTNER);
user.setActive(true);

clientRepo.save(user);

// CREATE ACTIVATION TOKEN
String token=UUID.randomUUID().toString();

PartnerActivationToken t=
new PartnerActivationToken();

t.setToken(token);
t.setPartnerId(saved.getId());
t.setExpiry(
LocalDateTime.now().plusDays(1)
);

tokenRepo.save(t);

// SEND EMAIL LINK
emailService.sendActivationLink(
saved.getEmail(),
"https://crm.app/set-password?token="+token
);


return saved;
}

// APPROVE PARTNER
public Partner approve(Long id){

Partner p=repo.findById(id)
.orElseThrow();

if(!p.getKycVerified()){
throw new RuntimeException(
"KYC Mandatory");
}

p.setActive(true);
return repo.save(p);
}

// SUSPEND
public Partner suspend(Long id){

Partner p=repo.findById(id)
.orElseThrow();

p.setActive(false);
return repo.save(p);
}

// SOFT DELETE
public void delete(Long id){

Partner p=repo.findById(id)
.orElseThrow();

p.setDeleted(true);
repo.save(p);
}
public Partner verifyKyc(Long id){

Partner p=repo.findById(id)
.orElseThrow();

p.setKycVerified(true);
return repo.save(p);
}

}
