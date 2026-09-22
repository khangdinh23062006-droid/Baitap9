package vn.iotstar.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    private final JavaMailSender mailSender;

    public void sendOtpEmail(String toEmail, String otpCode, String type) {
        String subject = "Xác nhận mã OTP - IOTSTAR SHOP";
        String content = "Mã OTP xác thực của bạn cho tác vụ [" + type + "] là: " + otpCode
                + "\nMã có hiệu lực trong 5 phút. Vui lòng không chia sẻ mã này cho bất kỳ ai.";

        // Always log prominently to console for easy testing & offline verification
        log.info("==================================================================");
        log.info("[OTP SERVICE] Gửi mã OTP đến: {}", toEmail);
        log.info("[OTP SERVICE] Tác vụ: {}", type);
        log.info("[OTP SERVICE] >>> MÃ OTP LÀ: {} <<<", otpCode);
        log.info("==================================================================");

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject(subject);
            message.setText(content);
            mailSender.send(message);
            log.info("Đã gửi email SMTP thành công tới {}", toEmail);
        } catch (Exception e) {
            log.warn("Không thể gửi email qua SMTP (kiểm tra internet hoặc cấu hình mail trong application.properties): {}", e.getMessage());
        }
    }
}
