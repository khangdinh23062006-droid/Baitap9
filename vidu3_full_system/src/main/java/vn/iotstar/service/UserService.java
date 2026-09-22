package vn.iotstar.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.iotstar.dto.UserDTO;

public interface UserService {
    Page<UserDTO> findAll(Pageable pageable);
    Page<UserDTO> searchUsers(String keyword, Pageable pageable);
    UserDTO findById(Long id);
    UserDTO createUser(UserDTO dto, String password, Long roleId);
    UserDTO updateUser(Long id, UserDTO dto, Long roleId);
    void deleteUser(Long id);
    void toggleUserStatus(Long id);
    long countTotalUsers();
}
