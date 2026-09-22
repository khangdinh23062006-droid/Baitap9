package vn.iotstar.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VerifyOtpDTO {
    @NotBlank(message = "Email không được để trống")
    @Email
    private String email;

    @NotBlank(message = "Mã OTP không được để trống")
    private String otpCode;
}
