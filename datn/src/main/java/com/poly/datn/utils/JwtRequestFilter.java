package com.poly.datn.utils;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.poly.datn.entities.Users;
import com.poly.datn.repository.UserRepository;
import com.poly.datn.security.CustomUserDetails;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        final String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer")) {
            String jwtToken = authorizationHeader.substring(7);
            try {
                if (jwtTokenUtil.isTokenValid(jwtToken)) {
                    String username = jwtTokenUtil.extractUsername(jwtToken);
                    Users user = userRepository.findByUsername(username)
                            .orElseThrow(() -> new UsernameNotFoundException("Người dùng không tồn tại: " + username));

                    List<GrantedAuthority> authorities = new ArrayList<>();
                    if (user.getRole() != null) {
                        String roleName = user.getRole().getRoleName();
                        if (roleName.equalsIgnoreCase("Admin")) {
                            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
                        } else if (roleName.equalsIgnoreCase("Admin kho")) {
                            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN_KHO"));
                        } else if (roleName.equalsIgnoreCase("Staff")) {
                            authorities.add(new SimpleGrantedAuthority("ROLE_STAFF"));
                        } else if (roleName.equalsIgnoreCase("Customer")) {
                            authorities.add(new SimpleGrantedAuthority("ROLE_CUSTOMER"));
                        }
                    }

                    CustomUserDetails customUserDetails = new CustomUserDetails(user, authorities);
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            customUserDetails, null, customUserDetails.getAuthorities());

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (ExpiredJwtException e) {
                response.sendError(HttpStatus.UNAUTHORIZED.value(), "Token hết hạn");
                return;
            } catch (MalformedJwtException e) {
                response.sendError(HttpStatus.UNAUTHORIZED.value(), "Token không hợp lệ");
                return;
            } catch (Exception e) {
                response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Lỗi trong xử lý token");
                return;
            }
        }

        chain.doFilter(request, response);
    }

}
