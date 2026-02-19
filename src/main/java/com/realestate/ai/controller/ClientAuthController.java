package com.realestate.ai.controller;

import com.realestate.ai.model.ClientUser;
import com.realestate.ai.model.Partner;
import com.realestate.ai.repository.ClientUserRepository;
import com.realestate.ai.repository.PartnerRepository;
import com.realestate.ai.security.JwtUtil;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/client")
public class ClientAuthController {

    private final ClientUserRepository clientRepo;
    private final PartnerRepository partnerRepo;
    private final JwtUtil jwtUtil;

    public ClientAuthController(
            ClientUserRepository clientRepo,
            PartnerRepository partnerRepo,
            JwtUtil jwtUtil){

        this.clientRepo = clientRepo;
        this.partnerRepo = partnerRepo;
        this.jwtUtil = jwtUtil;
    }

    // ================= PARTNER LOGIN =================
    @PostMapping("/login")
    public Map<String,String> login(
            @RequestBody Map<String,String> body){

        String email = body.get("email");
        String password = body.get("password");

        ClientUser user =
                clientRepo.findByEmail(email)
                        .orElseThrow(() ->
                                new RuntimeException("User Not Found"));

        if(!user.getPassword().equals(password)){
            throw new RuntimeException("Invalid Password");
        }

        // 🔥 CHECK PARTNER ACTIVE
        Partner p =
                partnerRepo.findByEmail(user.getEmail())
                        .orElseThrow(() ->
                                new RuntimeException("Partner Not Found"));

        if(!p.getActive()){
            throw new RuntimeException(
                    "Partner Not Approved");
        }

        String jwt =
                jwtUtil.generateToken(
                        user.getEmail(),
                        "PARTNER"
                );

        return Map.of(
                "token", jwt,
                "role", "PARTNER"
        );
    }
}
