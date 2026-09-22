package vn.iotstar.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import vn.iotstar.security.CustomUserDetails;
import vn.iotstar.service.ProductService;
import vn.iotstar.service.UserService;

@Controller
@RequiredArgsConstructor
public class HomeController {
    private final UserService userService;
    private final ProductService productService;

    @GetMapping("/")
    public String home(Model model, @AuthenticationPrincipal CustomUserDetails currentUser) {
        long totalUsers = userService.countTotalUsers();
        long totalProducts = productService.countTotalProducts();

        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("totalProducts", totalProducts);

        if (currentUser != null) {
            long userProducts = productService.countProductsByUser(currentUser.getId());
            model.addAttribute("userProductCount", userProducts);
        }

        // Get 6 featured products
        var latestProducts = productService.findAll(PageRequest.of(0, 6, Sort.by("id").descending())).getContent();
        model.addAttribute("latestProducts", latestProducts);

        return "home";
    }
}
