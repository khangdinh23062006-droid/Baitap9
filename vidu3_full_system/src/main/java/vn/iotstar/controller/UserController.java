package vn.iotstar.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.iotstar.dto.UserDTO;
import vn.iotstar.repository.RoleRepository;
import vn.iotstar.service.UserService;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final RoleRepository roleRepository;

    @GetMapping
    public String listUsers(@RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
                            @RequestParam(value = "page", defaultValue = "0") int page,
                            @RequestParam(value = "size", defaultValue = "5") int size,
                            Model model) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<UserDTO> userPage = userService.searchUsers(keyword, pageable);

        model.addAttribute("users", userPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("totalUsers", userService.countTotalUsers());
        return "users/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("userDTO", new UserDTO());
        model.addAttribute("roles", roleRepository.findAll());
        return "users/form";
    }

    @PostMapping("/save")
    public String saveUser(@Valid @ModelAttribute("userDTO") UserDTO dto,
                           BindingResult result,
                           @RequestParam(value = "password", required = false) String password,
                           @RequestParam("roleId") Long roleId,
                           RedirectAttributes redirectAttributes,
                           Model model) {
        if (result.hasErrors()) {
            model.addAttribute("roles", roleRepository.findAll());
            return "users/form";
        }
        try {
            if (dto.getId() == null) {
                if (password == null || password.trim().length() < 6) {
                    model.addAttribute("errorMessage", "Mật khẩu cho người dùng mới tối thiểu 6 ký tự!");
                    model.addAttribute("roles", roleRepository.findAll());
                    return "users/form";
                }
                userService.createUser(dto, password, roleId);
                redirectAttributes.addFlashAttribute("successMessage", "Thêm người dùng mới thành công!");
            } else {
                userService.updateUser(dto.getId(), dto, roleId);
                redirectAttributes.addFlashAttribute("successMessage", "Cập nhật người dùng thành công!");
            }
            return "redirect:/users";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("roles", roleRepository.findAll());
            return "users/form";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        UserDTO dto = userService.findById(id);
        model.addAttribute("userDTO", dto);
        model.addAttribute("roles", roleRepository.findAll());
        return "users/form";
    }

    @GetMapping("/toggle/{id}")
    public String toggleStatus(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.toggleUserStatus(id);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật trạng thái người dùng thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/users";
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.deleteUser(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa người dùng thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không thể xóa người dùng này: " + e.getMessage());
        }
        return "redirect:/users";
    }
}
