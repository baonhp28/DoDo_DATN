package com.poly.datn.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.poly.datn.entities.PasswordResetToken;
import com.poly.datn.entities.Users;
import com.poly.datn.repository.PasswordResetTokenRepository;
import com.poly.datn.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class PasswordResetService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Phương thức sinh mã 6 số ngẫu nhiên
    private String generateVerificationCode() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000)); // Sinh số ngẫu nhiên từ 000000 đến 999999
    }

    // Gửi mã xác nhận 6 số đến email người dùng
    public void generatePasswordResetToken(String email) {
        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Email không tồn tại"));

        // Sinh mã xác thực 6 số
        String verificationCode = generateVerificationCode();
        
        // Tạo đối tượng PasswordResetToken để lưu mã xác thực và thời gian hết hạn
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setUser(user);  // Liên kết với người dùng
        resetToken.setToken(verificationCode);  // Lưu mã xác thực là 6 số
        resetToken.setExpiryDate(LocalDateTime.now().plusMinutes(10));  // Hết hạn sau 10 phút

        // Lưu mã xác thực vào cơ sở dữ liệu
        passwordResetTokenRepository.save(resetToken);

        // Gửi email chứa mã xác thực
        String emailContent = "Mã xác nhận để đặt lại mật khẩu của bạn là: " + verificationCode + ". Mã này sẽ hết hạn sau 10 phút.";
        emailService.sendEmail(user.getEmail(), "Mã xác nhận đặt lại mật khẩu", emailContent);
    }

    // Đặt lại mật khẩu với mã xác thực 6 số và mật khẩu mới
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Mã xác thực không hợp lệ"));
    
        if (resetToken.isExpired()) {
            throw new IllegalArgumentException("Mã xác thực đã hết hạn");
        }
    
        Users user = resetToken.getUser();
    
        // Mã hóa mật khẩu mới
        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);
    
        // Lưu mật khẩu mới
        userRepository.save(user);
    
        // Xóa mã xác thực sau khi sử dụng
        passwordResetTokenRepository.delete(resetToken);
    }
    

    // Tìm người dùng dựa trên mã xác thực
    public Users findByToken(String verificationCode) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(verificationCode)
                .orElse(null);
        if (resetToken != null) {
            return resetToken.getUser(); // Trả về người dùng nếu mã xác thực hợp lệ
        }
        return null;
    }

    // Xóa mã xác thực khỏi cơ sở dữ liệu
    @Transactional
    public void deleteByToken(String verificationCode) {
        Optional<PasswordResetToken> resetToken = passwordResetTokenRepository.findByToken(verificationCode);
        resetToken.ifPresent(passwordResetTokenRepository::delete);
    }
}
