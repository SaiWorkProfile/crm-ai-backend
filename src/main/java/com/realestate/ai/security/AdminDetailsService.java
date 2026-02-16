package com.realestate.ai.security;

import com.realestate.ai.model.Admin;
import com.realestate.ai.repository.AdminRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminDetailsService implements UserDetailsService {

    private final AdminRepository adminRepository;

    public AdminDetailsService(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        Admin admin = adminRepository
                .findByEmail(email)
                .orElseThrow(() ->
                    new UsernameNotFoundException("Admin not found"));

        return new User(
                admin.getEmail(),
                admin.getPassword(),
                List.of(
                    new SimpleGrantedAuthority(
                        admin.getRole().name()
                    )
                )
        );
    }
}
