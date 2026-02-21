package com.realestate.ai.controller;

import com.realestate.ai.model.ClientUser;
import com.realestate.ai.model.ClientRole;
import com.realestate.ai.repository.ClientUserRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping("/api/admin/clients")
@PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN') or hasAuthority('ROLE_ADMIN')")
public class AdminClientController {

    private final ClientUserRepository clientRepo;

    public AdminClientController(ClientUserRepository clientRepo){
        this.clientRepo = clientRepo;
    }

    // ================= CREATE =================
    @PostMapping
    public ClientUser create(@RequestBody ClientUser user){

        user.setRole(ClientRole.CLIENT);
        user.setActive(true);

        return clientRepo.save(user);
    }

    // ================= GET =================
    @GetMapping
    public List<ClientUser> all(){
        return clientRepo.findAll();
    }

    // ================= SUSPEND =================
    @PutMapping("/{id}/suspend")
    public ClientUser suspend(@PathVariable Long id){

        ClientUser user = clientRepo.findById(id)
                .orElseThrow();

        user.setActive(false);

        return clientRepo.save(user);
    }

    // ================= DELETE =================
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id){

        clientRepo.deleteById(id);
    }
}