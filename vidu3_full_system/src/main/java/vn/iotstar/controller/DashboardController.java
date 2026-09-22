package vn.iotstar.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import vn.iotstar.service.ProductService;
import vn.iotstar.service.UserService;

@Controller
@RequiredArgsConstructor
public class DashboardController {
    private final UserService userService;
    private final ProductService productService;

    @GetMapping("/admin/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("totalUsers", userService.countTotalUsers());
        model.addAttribute("totalProducts", productService.countTotalProducts());

        model.addAttribute("recentUsers", userService.findAll(PageRequest.of(0, 5, Sort.by("id").descending())).getContent());
        model.addAttribute("recentProducts", productService.findAll(PageRequest.of(0, 5, Sort.by("id").descending())).getContent());

        return "admin/dashboard";
    }
}
