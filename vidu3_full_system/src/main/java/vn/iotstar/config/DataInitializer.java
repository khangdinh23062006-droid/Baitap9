package vn.iotstar.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import vn.iotstar.entity.Product;
import vn.iotstar.entity.Role;
import vn.iotstar.entity.User;
import vn.iotstar.repository.ProductRepository;
import vn.iotstar.repository.RoleRepository;
import vn.iotstar.repository.UserRepository;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner initDatabase() {
        return args -> {
            Role roleAdmin = roleRepository.findByName("ROLE_ADMIN").orElseGet(() ->
                    roleRepository.save(Role.builder().name("ROLE_ADMIN").build()));
            Role roleUser = roleRepository.findByName("ROLE_USER").orElseGet(() ->
                    roleRepository.save(Role.builder().name("ROLE_USER").build()));

            User admin = userRepository.findByUsername("admin").orElseGet(() -> {
                User u = User.builder()
                        .username("admin")
                        .email("admin@hcmute.edu.vn")
                        .password(passwordEncoder.encode("123456"))
                        .fullName("Quản Trị Viên Hệ Thống")
                        .images("/images/avatar-default.png")
                        .role(roleAdmin)
                        .enabled(true)
                        .build();
                return userRepository.save(u);
            });

            User user1 = userRepository.findByUsername("user01").orElseGet(() -> {
                User u = User.builder()
                        .username("user01")
                        .email("user01@gmail.com")
                        .password(passwordEncoder.encode("123456"))
                        .fullName("Nguyễn Hữu Trung")
                        .images("/images/user.png")
                        .role(roleUser)
                        .enabled(true)
                        .build();
                return userRepository.save(u);
            });

            User user2 = userRepository.findByUsername("user02").orElseGet(() -> {
                User u = User.builder()
                        .username("user02")
                        .email("user02@gmail.com")
                        .password(passwordEncoder.encode("123456"))
                        .fullName("Đinh Thế Khang")
                        .images("/images/user.png")
                        .role(roleUser)
                        .enabled(true)
                        .build();
                return userRepository.save(u);
            });

            if (productRepository.count() == 0) {
                productRepository.save(Product.builder()
                        .name("Laptop Dell XPS 15")
                        .brand("Dell")
                        .madein("Mỹ")
                        .price(35000000.0)
                        .description("Laptop doanh nhân cao cấp màn hình OLED 4K cực đẹp.")
                        .images("/images/default-product.png")
                        .user(user1)
                        .build());

                productRepository.save(Product.builder()
                        .name("Bàn phím cơ Keychron K2 Pro")
                        .brand("Keychron")
                        .madein("Trung Quốc")
                        .price(2100000.0)
                        .description("Bàn phím cơ không dây layout 75%, hot-swap, RGB đẹp mắt.")
                        .images("/images/default-product.png")
                        .user(user1)
                        .build());

                productRepository.save(Product.builder()
                        .name("Chuột Logitech MX Master 3S")
                        .brand("Logitech")
                        .madein("Thụy Sĩ")
                        .price(2450000.0)
                        .description("Chuột công thái học cao cấp dành cho lập trình viên và designer.")
                        .images("/images/default-product.png")
                        .user(user2)
                        .build());

                productRepository.save(Product.builder()
                        .name("Màn hình LG UltraFine 27 inch 4K")
                        .brand("LG")
                        .madein("Hàn Quốc")
                        .price(11500000.0)
                        .description("Màn hình đồ họa màu sắc chuẩn IPS, cổng kết nối Type-C PD 90W.")
                        .images("/images/default-product.png")
                        .user(user2)
                        .build());

                productRepository.save(Product.builder()
                        .name("Tai nghe Sony WH-1000XM5")
                        .brand("Sony")
                        .madein("Nhật Bản")
                        .price(8490000.0)
                        .description("Tai nghe chống ồn chủ động đỉnh cao, thời lượng pin 30 giờ liên tục.")
                        .images("/images/default-product.png")
                        .user(admin)
                        .build());
            }

            log.info(">> Khởi tạo dữ liệu mẫu thành công: Users={}, Products={}",
                    userRepository.count(), productRepository.count());
        };
    }
}
