package vn.iotstar.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import vn.iotstar.dto.RegisterDTO;
import vn.iotstar.dto.ResetPasswordDTO;
import vn.iotstar.entity.OtpToken;
import vn.iotstar.entity.Role;
import vn.iotstar.entity.User;
import vn.iotstar.repository.OtpTokenRepository;
import vn.iotstar.repository.RoleRepository;
import vn.iotstar.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final OtpTokenRepository otpTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final CloudinaryService cloudinaryService;

    @Transactional
    public void register(RegisterDTO dto, MultipartFile avatar) {
        if (userRepository.existsByUsernameIgnoreCase(dto.getUsername())) {
            throw new IllegalArgumentException("Username đã tồn tại trên hệ thống!");
        }
        if (userRepository.existsByEmailIgnoreCase(dto.getEmail())) {
            throw new IllegalArgumentException("Email đã tồn tại trên hệ thống!");
        }
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new IllegalArgumentException("Mật khẩu xác nhận không khớp!");
        }

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseGet(() -> roleRepository.save(Role.builder().name("ROLE_USER").build()));

        String avatarUrl = "/images/avatar-default.png";
        if (avatar != null && !avatar.isEmpty()) {
            String uploaded = cloudinaryService.uploadImage(avatar, "avatars");
            if (uploaded != null) avatarUrl = uploaded;
        }

        User user = User.builder()
                .username(dto.getUsername().trim())
                .email(dto.getEmail().trim().toLowerCase())
                .password(passwordEncoder.encode(dto.getPassword()))
                .fullName(dto.getFullName().trim())
                .images(avatarUrl)
                .role(userRole)
                .enabled(false) // Account disabled until OTP is verified
                .build();

        userRepository.save(user);

        // Generate OTP & Send Email
        generateAndSendOtp(user, "REGISTER");
    }

    @Transactional
    public void generateAndSendOtp(User user, String type) {
        String code = String.format("%06d", new Random().nextInt(999999));
        OtpToken otpToken = OtpToken.builder()
                .user(user)
                .otpCode(code)
                .type(type)
                .expiryTime(LocalDateTime.now().plusMinutes(5))
                .isUsed(false)
                .build();
        otpTokenRepository.save(otpToken);
        emailService.sendOtpEmail(user.getEmail(), code, type);
    }

    @Transactional
    public void verifyRegisterOtp(String email, String otpCode) {
        User user = userRepository.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người dùng với email: " + email));

        OtpToken token = otpTokenRepository.findByUserAndOtpCodeAndTypeAndIsUsedFalse(user, otpCode.trim(), "REGISTER")
                .orElseThrow(() -> new IllegalArgumentException("Mã OTP không đúng hoặc đã được sử dụng!"));

        if (token.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Mã OTP đã hết hạn! Vui lòng yêu cầu gửi lại mã mới.");
        }

        token.setUsed(true);
        otpTokenRepository.save(token);

        user.setEnabled(true);
        userRepository.save(user);
        log.info("Người dùng {} đã xác thực tài khoản thành công!", user.getUsername());
    }

    @Transactional
    public void processForgotPassword(String email) {
        User user = userRepository.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new IllegalArgumentException("Email không tồn tại trên hệ thống!"));

        generateAndSendOtp(user, "FORGOT_PASSWORD");
    }

    @Transactional
    public void resetPassword(ResetPasswordDTO dto) {
        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            throw new IllegalArgumentException("Mật khẩu xác nhận không khớp!");
        }

        User user = userRepository.findByEmail(dto.getEmail().trim().toLowerCase())
                .orElseThrow(() -> new IllegalArgumentException("Email không tồn tại!"));

        OtpToken token = otpTokenRepository.findByUserAndOtpCodeAndTypeAndIsUsedFalse(user, dto.getOtpCode().trim(), "FORGOT_PASSWORD")
                .orElseThrow(() -> new IllegalArgumentException("Mã OTP không hợp lệ hoặc đã hết hạn!"));

        if (token.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Mã OTP đã hết hạn!");
        }

        token.setUsed(true);
        otpTokenRepository.save(token);

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);
        log.info("Người dùng {} đã đổi mật khẩu thành công!", user.getUsername());
    }

    @Transactional
    public void resendOtp(String email, String type) {
        User user = userRepository.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy email: " + email));
        generateAndSendOtp(user, type);
    }
}
