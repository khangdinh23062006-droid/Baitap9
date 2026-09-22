package vn.iotstar.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import vn.iotstar.dto.ProductDTO;
import vn.iotstar.security.CustomUserDetails;

public interface ProductService {
    Page<ProductDTO> findAll(Pageable pageable);
    Page<ProductDTO> searchProducts(String keyword, Pageable pageable);
    Page<ProductDTO> findByUser(Long userId, Pageable pageable);
    ProductDTO findById(Long id);
    ProductDTO saveProduct(ProductDTO dto, MultipartFile imageFile, CustomUserDetails currentUser);
    void deleteProduct(Long id, CustomUserDetails currentUser);
    long countTotalProducts();
    long countProductsByUser(Long userId);
}
