package com.realestate.ai.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.security.access.prepost.PreAuthorize;
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
@PreAuthorize("hasAnyAuthority('ROLE_SUPER_ADMIN','ROLE_ADMIN')")
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

// ================= ONBOARD =================
//================= ONBOARD =================
public Partner onboard(Partner p){

System.out.println("🚀 SERVICE HIT: ONBOARD PARTNER");

try {

if(p.getPartnerType()==PartnerType.LEGACY_PARTNER){

repo.findByPincodeAndDeletedFalse(
p.getPincode())
.ifPresent(x->{
throw new RuntimeException(
"Legacy Partner exists for this pincode");
});
}

Partner saved=repo.save(p);
System.out.println("✅ PARTNER SAVED ID: "+saved.getId());


//🔥 CREATE LOGIN ENTRY (INACTIVE)
ClientUser user = new ClientUser();

user.setName(saved.getName());
user.setEmail(saved.getEmail());
user.setPhone(saved.getPhone());
user.setRole(ClientRole.PARTNER);
user.setActive(false);        // ❗EMAIL NOT VERIFIED YET
user.setPassword(null);

clientRepo.save(user);
System.out.println("✅ PARTNER USER CREATED");


//🔥 CREATE ACTIVATION TOKEN
String token=UUID.randomUUID().toString();

PartnerActivationToken t=
new PartnerActivationToken();

t.setToken(token);
t.setPartnerId(saved.getId());
t.setExpiry(
LocalDateTime.now().plusHours(24)
);

tokenRepo.save(t);
System.out.println("✅ TOKEN CREATED");


//🔥 SEND CORRECT EMAIL LINK
try {

emailService.sendActivationLink(
saved.getEmail(),
"https://manortha-website.vercel.app/partner/set-password?token=" + token
);

System.out.println("✅ EMAIL SENT");

}catch(Exception e){

System.out.println(
"⚠ EMAIL FAILED: "
+ e.getMessage()
);

}

return saved;

}catch(Exception ex){

System.out.println("🔥 SERVICE ERROR: "+ex.getMessage());
throw ex;
}
}
// ================= APPROVE =================
public Partner approve(Long id){

System.out.println("🚀 SERVICE HIT: APPROVE");

Partner p=repo.findById(id)
.orElseThrow();

if(!p.getKycVerified()){

System.out.println("❌ KYC NOT VERIFIED");

throw new RuntimeException(
"KYC Mandatory");
}

p.setActive(true);
return repo.save(p);
}

// ================= SUSPEND =================
public Partner suspend(Long id){

System.out.println("🚀 SERVICE HIT: SUSPEND");

Partner p=repo.findById(id)
.orElseThrow();

p.setActive(false);
return repo.save(p);
}

// ================= VERIFY =================
public Partner verifyKyc(Long id){

System.out.println("🚀 SERVICE HIT: VERIFY KYC");

Partner p=repo.findById(id)
.orElseThrow();

p.setKycVerified(true);
return repo.save(p);
}

// ================= DELETE =================
public void delete(Long id){

System.out.println("🚀 SERVICE HIT: DELETE");

Partner p=repo.findById(id)
.orElseThrow();

p.setDeleted(true);
repo.save(p);
}
}