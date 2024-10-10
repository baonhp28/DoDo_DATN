package com.poly.datn.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.poly.datn.utils.JwtRequestFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private AuthenticationConfiguration authenticationConfiguration;

    @Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
    .csrf().disable()
    .authorizeHttpRequests(authorizeRequests -> authorizeRequests
        // Auth và User API
        .requestMatchers(HttpMethod.OPTIONS, "/api/**").permitAll()  // Cho phép mọi yêu cầu OPTIONS
        .requestMatchers("/api/auth/**", "/api/user/**").permitAll()
        // Quyền cho API Users
        .requestMatchers(HttpMethod.GET, "/api/users").authenticated() // Chỉ người dùng đã đăng nhập có thể lấy thông tin người dùng
        .requestMatchers(HttpMethod.PUT, "/api/users/update").authenticated() // Chỉ người dùng đã đăng nhập có thể cập nhật thông tin của chính mình

        // Quyền cho API Vouchers
        .requestMatchers(HttpMethod.POST, "/api/vouchers").hasAnyRole("ADMIN", "STAFF") // Admin và Staff có thể thêm voucher
        .requestMatchers(HttpMethod.PUT, "/api/vouchers/{id}").hasAnyRole("ADMIN", "STAFF") // Admin và Staff có thể cập nhật voucher
        .requestMatchers(HttpMethod.DELETE, "/api/vouchers/{id}").hasRole("ADMIN") // Chỉ Admin có thể xóa voucher
        .requestMatchers(HttpMethod.GET, "/api/vouchers").permitAll() // Ai cũng có thể xem danh sách voucher
        .requestMatchers(HttpMethod.GET, "/api/vouchers/{id}").permitAll() // Ai cũng có thể xem thông tin voucher theo ID

        // Admin API
        .requestMatchers(HttpMethod.GET, "/api/admin/employee").hasRole("ADMIN") // Chỉ Admin có thể xem danh sách nhân viên
        .requestMatchers(HttpMethod.POST, "/api/admin/employee").hasRole("ADMIN") // Chỉ Admin có thể thêm nhân viên
        .requestMatchers(HttpMethod.PUT, "/api/admin/changeAccountStatus/{id}").hasRole("ADMIN") // Chỉ Admin có thể thay đổi trạng thái tài khoản
        .requestMatchers(HttpMethod.PUT, "/api/admin/employees/{id}").hasRole("ADMIN") // Chỉ Admin có thể cập nhật thông tin nhân viên
        .requestMatchers(HttpMethod.DELETE, "/api/admin/employees/{id}").hasRole("ADMIN") // Chỉ Admin có thể xóa nhân viên

        // Inventory API (API Kho)
        .requestMatchers(HttpMethod.GET, "/api/inventory/**").hasAnyRole("ADMIN", "ADMIN_KHO") // Chỉ Admin và Admin kho có thể xem thông tin kho
        .requestMatchers(HttpMethod.POST, "/api/inventory/**").hasAnyRole("ADMIN", "ADMIN_KHO") // Chỉ Admin kho có thể thêm thông tin kho
        .requestMatchers(HttpMethod.PUT, "/api/inventory/**").hasAnyRole("ADMIN", "ADMIN_KHO") // Chỉ Admin kho có thể cập nhật thông tin kho
        .requestMatchers(HttpMethod.DELETE, "/api/inventory/**").hasRole("ADMIN") // Chỉ Admin có thể xóa thông tin kho

        // Orders API
        .requestMatchers(HttpMethod.GET, "/api/orders/admin/**").hasAnyRole("ADMIN", "STAFF") // Admin và Staff có thể xem danh sách đơn hàng
        .requestMatchers(HttpMethod.PUT, "/api/orders/admin/**").hasRole("STAFF") // Staff có thể cập nhật trạng thái đơn hàng
        .requestMatchers(HttpMethod.DELETE, "/api/orders/admin/**").hasRole("ADMIN") // Chỉ Admin có thể xóa đơn hàng

        // Customer Orders
        .requestMatchers(HttpMethod.POST, "/api/orders/customer").hasRole("CUSTOMER") // Khách hàng tạo đơn hàng
        .requestMatchers(HttpMethod.GET, "/api/orders/customer/{id}").hasRole("CUSTOMER") // Khách hàng xem đơn hàng của mình

        // Quyền cho API Categories
        .requestMatchers(HttpMethod.GET, "/api/categories/**").permitAll() // Cho phép tất cả người dùng xem danh mục
        .requestMatchers(HttpMethod.POST, "/api/categories/**").hasRole("ADMIN") // Chỉ Admin có thể thêm danh mục
        .requestMatchers(HttpMethod.PUT, "/api/categories/**").hasRole("ADMIN") // Chỉ Admin có thể cập nhật danh mục
        .requestMatchers(HttpMethod.DELETE, "/api/categories/**").hasRole("ADMIN") // Chỉ Admin có thể xóa danh mục

        // Quyền cho API Products
        .requestMatchers(HttpMethod.GET, "/api/products").permitAll() // Ai cũng có thể xem sản phẩm
        .requestMatchers(HttpMethod.GET, "/api/products/{id}").permitAll() // Ai cũng có thể xem chi tiết sản phẩm
        .requestMatchers(HttpMethod.POST, "/api/products").hasRole("ADMIN") // Chỉ Admin có thể thêm sản phẩm
        .requestMatchers(HttpMethod.PUT, "/api/products/{id}").hasRole("ADMIN") // Chỉ Admin có thể cập nhật sản phẩm
        .requestMatchers(HttpMethod.DELETE, "/api/products/{id}").hasRole("ADMIN") // Chỉ Admin có thể xóa sản phẩm

        // Quyền cho API SKUs
        .requestMatchers(HttpMethod.POST, "/api/searchSkusByPrice").authenticated() // Chỉ người dùng đã đăng nhập có thể tìm kiếm SKUs theo giá
        .requestMatchers(HttpMethod.GET, "/api/allSkus").hasAnyRole("ADMIN", "ADMIN_KHO") // Chỉ Admin và Admin kho có thể lấy tất cả SKUs

        // Bất kỳ request nào khác cần được xác thực
        .anyRequest().authenticated()
    )
    .formLogin().disable()
    .httpBasic()
    .and()
    .exceptionHandling()
    .accessDeniedHandler(accessDeniedHandler());

    http.addFilterBefore(jwtRequestFilter(), UsernamePasswordAuthenticationFilter.class);

    return http.build();
}


@Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                        .allowedOrigins("http://localhost:3000")  // Địa chỉ frontend
                        .allowedMethods("GET", "POST", "PUT", "DELETE")  // Các phương thức được phép
                        .allowedHeaders("*")  // Các headers được phép
                        .allowCredentials(true)  // Cho phép credentials (nếu cần)
                        .maxAge(3600);  // Thời gian cache preflight request
            }
        };
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, accessDeniedException) -> {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setContentType("application/json;charset=UTF-8");

            String message = "Bạn không có quyền thực hiện chức năng này.";

            response.getWriter().write("{\"message\": \"" + message + "\"}");
        };
    }

    @Bean
    public JwtRequestFilter jwtRequestFilter() {
        return new JwtRequestFilter();
    }

    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
