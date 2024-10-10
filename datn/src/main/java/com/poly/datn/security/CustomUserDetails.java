package com.poly.datn.security;

import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import com.poly.datn.entities.Users;

public class CustomUserDetails implements UserDetails {
    private Users user;
    private Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(Users user, Collection<? extends GrantedAuthority> authorities) {
        this.user = user;
        this.authorities = authorities;
    }

    public Integer getId() {
        return user.getId();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        System.out.println("Authorities: " + authorities); // Log để kiểm tra quyền
        return authorities;
    }
    

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return user.getStatus(); // Điều chỉnh theo phương thức hoặc thuộc tính thực tế trong thực thể Users của bạn
    }
}
