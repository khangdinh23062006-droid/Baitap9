package vn.iotstar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.iotstar.entity.OtpToken;
import vn.iotstar.entity.User;

import java.util.Optional;

@Repository
public interface OtpTokenRepository extends JpaRepository<OtpToken, Long> {
    Optional<OtpToken> findTopByUserAndTypeAndIsUsedFalseOrderByExpiryTimeDesc(User user, String type);
    Optional<OtpToken> findByUserAndOtpCodeAndTypeAndIsUsedFalse(User user, String otpCode, String type);
}
