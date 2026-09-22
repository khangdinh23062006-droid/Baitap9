package vn.iotstar.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.iotstar.dto.ProductDTO;
import vn.iotstar.security.CustomUserDetails;
import vn.iotstar.service.ProductService;

@Controller
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping("/products")
    public String listProducts(@RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
                               @RequestParam(value = "page", defaultValue = "0") int page,
                               @RequestParam(value = "size", defaultValue = "6") int size,
                               Model model) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<ProductDTO> products = productService.searchProducts(keyword, pageable);

        model.addAttribute("products", products);
        model.addAttribute("keyword", keyword);
        model.addAttribute("totalProducts", productService.countTotalProducts());
        model.addAttribute("isMyProducts", false);
        return "products/list";
    }

    @GetMapping("/my-products")
    public String listMyProducts(@AuthenticationPrincipal CustomUserDetails currentUser,
                                 @RequestParam(value = "page", defaultValue = "0") int page,
                                 @RequestParam(value = "size", defaultValue = "6") int size,
                                 Model model) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<ProductDTO> products = productService.findByUser(currentUser.getId(), pageable);

        model.addAttribute("products", products);
        model.addAttribute("totalProducts", productService.countProductsByUser(currentUser.getId()));
        model.addAttribute("isMyProducts", true);
        return "products/list";
    }

    @GetMapping("/products/detail/{id}")
    public String productDetail(@PathVariable("id") Long id, Model model) {
        model.addAttribute("product", productService.findById(id));
        return "products/detail";
    }

    @GetMapping("/products/new")
    public String showCreateForm(Model model) {
        model.addAttribute("productDTO", new ProductDTO());
        return "products/form";
    }

    @PostMapping("/products/save")
    public String saveProduct(@Valid @ModelAttribute("productDTO") ProductDTO dto,
                              BindingResult result,
                              @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                              @AuthenticationPrincipal CustomUserDetails currentUser,
                              RedirectAttributes redirectAttributes,
                              Model model) {
        if (result.hasErrors()) {
            return "products/form";
        }
        try {
            productService.saveProduct(dto, imageFile, currentUser);
            redirectAttributes.addFlashAttribute("successMessage", "Lưu sản phẩm thành công!");
            return "redirect:/products";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "products/form";
        }
    }

    @GetMapping("/products/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        model.addAttribute("productDTO", productService.findById(id));
        return "products/form";
    }

    @GetMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable("id") Long id,
                                @AuthenticationPrincipal CustomUserDetails currentUser,
                                RedirectAttributes redirectAttributes) {
        try {
            productService.deleteProduct(id, currentUser);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa sản phẩm thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/products";
    }
}
