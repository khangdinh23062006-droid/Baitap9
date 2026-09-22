package vn.iotstar.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.iotstar.dto.ForgotPasswordDTO;
import vn.iotstar.dto.RegisterDTO;
import vn.iotstar.dto.ResetPasswordDTO;
import vn.iotstar.dto.VerifyOtpDTO;
import vn.iotstar.service.AuthService;

@Controller
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                            @RequestParam(value = "logout", required = false) String logout,
                            Model model) {
        if (error != null) {
            model.addAttribute("errorMessage", "Tên đăng nhập hoặc mật khẩu không chính xác hoặc tài khoản chưa kích hoạt!");
        }
        if (logout != null) {
            model.addAttribute("successMessage", "Bạn đã đăng xuất an toàn khỏi hệ thống!");
        }
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("registerDTO", new RegisterDTO());
        return "auth/register";
    }

    @PostMapping("/register")
    public String handleRegister(@Valid @ModelAttribute("registerDTO") RegisterDTO dto,
                                 BindingResult result,
                                 @RequestParam(value = "avatarFile", required = false) MultipartFile avatar,
                                 RedirectAttributes redirectAttributes,
                                 Model model) {
        if (result.hasErrors()) {
            return "auth/register";
        }
        try {
            authService.register(dto, avatar);
            redirectAttributes.addFlashAttribute("successMessage", "Đăng ký thành công! Vui lòng nhập mã OTP đã gửi đến email của bạn để kích hoạt.");
            return "redirect:/verify-otp?email=" + dto.getEmail();
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "auth/register";
        }
    }

    @GetMapping("/verify-otp")
    public String verifyOtpPage(@RequestParam(value = "email", required = false, defaultValue = "") String email, Model model) {
        VerifyOtpDTO dto = new VerifyOtpDTO();
        dto.setEmail(email);
        model.addAttribute("verifyOtpDTO", dto);
        return "auth/verify-otp";
    }

    @PostMapping("/verify-otp")
    public String handleVerifyOtp(@Valid @ModelAttribute("verifyOtpDTO") VerifyOtpDTO dto,
                                  BindingResult result,
                                  RedirectAttributes redirectAttributes,
                                  Model model) {
        if (result.hasErrors()) {
            return "auth/verify-otp";
        }
        try {
            authService.verifyRegisterOtp(dto.getEmail(), dto.getOtpCode());
            redirectAttributes.addFlashAttribute("successMessage", "Xác thực OTP thành công! Tài khoản đã được kích hoạt. Hãy đăng nhập.");
            return "redirect:/login";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "auth/verify-otp";
        }
    }

    @PostMapping("/register/resend-otp")
    public String handleResendOtp(@RequestParam("email") String email,
                                  @RequestParam("type") String type,
                                  RedirectAttributes redirectAttributes) {
        try {
            authService.resendOtp(email, type);
            redirectAttributes.addFlashAttribute("successMessage", "Đã gửi lại mã OTP mới đến email: " + email);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        if ("FORGOT_PASSWORD".equalsIgnoreCase(type)) {
            return "redirect:/reset-password?email=" + email;
        }
        return "redirect:/verify-otp?email=" + email;
    }

    @GetMapping("/forgot-password")
    public String forgotPasswordPage(Model model) {
        model.addAttribute("forgotPasswordDTO", new ForgotPasswordDTO());
        return "auth/forgot-password";
    }

    @PostMapping("/forgot-password")
    public String handleForgotPassword(@Valid @ModelAttribute("forgotPasswordDTO") ForgotPasswordDTO dto,
                                       BindingResult result,
                                       RedirectAttributes redirectAttributes,
                                       Model model) {
        if (result.hasErrors()) {
            return "auth/forgot-password";
        }
        try {
            authService.processForgotPassword(dto.getEmail());
            redirectAttributes.addFlashAttribute("successMessage", "Mã OTP đặt lại mật khẩu đã được gửi đến email của bạn!");
            return "redirect:/reset-password?email=" + dto.getEmail();
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "auth/forgot-password";
        }
    }

    @GetMapping("/reset-password")
    public String resetPasswordPage(@RequestParam(value = "email", required = false, defaultValue = "") String email, Model model) {
        ResetPasswordDTO dto = new ResetPasswordDTO();
        dto.setEmail(email);
        model.addAttribute("resetPasswordDTO", dto);
        return "auth/reset-password";
    }

    @PostMapping("/reset-password")
    public String handleResetPassword(@Valid @ModelAttribute("resetPasswordDTO") ResetPasswordDTO dto,
                                      BindingResult result,
                                      RedirectAttributes redirectAttributes,
                                      Model model) {
        if (result.hasErrors()) {
            return "auth/reset-password";
        }
        try {
            authService.resetPassword(dto);
            redirectAttributes.addFlashAttribute("successMessage", "Đặt lại mật khẩu thành công! Hãy đăng nhập với mật khẩu mới.");
            return "redirect:/login";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "auth/reset-password";
        }
    }

    @GetMapping("/403")
    public String accessDenied() {
        return "403";
    }
}
