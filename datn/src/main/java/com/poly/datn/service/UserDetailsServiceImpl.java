package com.poly.datn.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.poly.datn.entities.Users;
import com.poly.datn.repository.UserRepository;
import com.poly.datn.security.CustomUserDetails; // Cập nhật đúng tên lớp CustomUserDetails
import java.util.ArrayList;
import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Người dùng không tồn tại: " + username));

        List<GrantedAuthority> authorities = new ArrayList<>();

        if (user.getRole() != null) {
            String roleName = user.getRole().getRoleName();
            if ("Admin".equalsIgnoreCase(roleName)) {
                authorities.add(new SimpleGrantedAuthority("ADMIN"));
            } else if ("Admin kho".equalsIgnoreCase(roleName)) {
                authorities.add(new SimpleGrantedAuthority("ADMIN_KHO"));
            } else if ("Staff".equalsIgnoreCase(roleName)) {
                authorities.add(new SimpleGrantedAuthority("STAFF"));
            } else if ("Customer".equalsIgnoreCase(roleName)) {
                authorities.add(new SimpleGrantedAuthority("CUSTOMER"));
            }
        }

        return new CustomUserDetails(user, authorities);
    }
}
