package vn.iotstar.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import vn.iotstar.entity.Role;
import vn.iotstar.entity.User;
import vn.iotstar.repository.RoleRepository;
import vn.iotstar.repository.UserRepository;

@Configuration
public class DataInitializer {
    @Bean
    CommandLineRunner init(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            Role roleUser = roleRepository.findByName("ROLE_USER").orElseGet(() ->
                    roleRepository.save(Role.builder().name("ROLE_USER").build())
            );

            Role roleAdmin = roleRepository.findByName("ROLE_ADMIN").orElseGet(() ->
                    roleRepository.save(Role.builder().name("ROLE_ADMIN").build())
            );

            if (userRepository.findByUsername("user01").isEmpty()) {
                User user = User.builder()
                        .username("user01")
                        .email("user01@gmail.com")
                        .password(passwordEncoder.encode("123456"))
                        .fullName("Nguyễn Hữu Trung")
                        .images("/images/user.png")
                        .role(roleUser)
                        .enabled(true)
                        .build();
                userRepository.save(user);
                System.out.println(">> Đã khởi tạo user01 thành công!");
            }

            if (userRepository.findByUsername("admin").isEmpty()) {
                User admin = User.builder()
                        .username("admin")
                        .email("admin@hcmute.edu.vn")
                        .password(passwordEncoder.encode("123456"))
                        .fullName("Quản Trị Viên Hệ Thống")
                        .images("/images/avatar-default.png")
                        .role(roleAdmin)
                        .enabled(true)
                        .build();
                userRepository.save(admin);
                System.out.println(">> Đã khởi tạo admin thành công!");
            }
        };
    }
}
