package vn.iotstar.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import vn.iotstar.dto.ProductDTO;
import vn.iotstar.entity.Product;
import vn.iotstar.entity.User;
import vn.iotstar.mapper.ProductMapper;
import vn.iotstar.repository.ProductRepository;
import vn.iotstar.repository.UserRepository;
import vn.iotstar.security.CustomUserDetails;
import vn.iotstar.service.CloudinaryService;
import vn.iotstar.service.ProductService;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ProductMapper productMapper;
    private final CloudinaryService cloudinaryService;

    @Override
    public Page<ProductDTO> findAll(Pageable pageable) {
        return productRepository.findAll(pageable).map(productMapper::toDto);
    }

    @Override
    public Page<ProductDTO> searchProducts(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return findAll(pageable);
        }
        return productRepository.searchProducts(keyword.trim(), pageable).map(productMapper::toDto);
    }

    @Override
    public Page<ProductDTO> findByUser(Long userId, Pageable pageable) {
        return productRepository.findByUserId(userId, pageable).map(productMapper::toDto);
    }

    @Override
    public ProductDTO findById(Long id) {
        Product p = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm id: " + id));
        return productMapper.toDto(p);
    }

    @Override
    @Transactional
    public ProductDTO saveProduct(ProductDTO dto, MultipartFile imageFile, CustomUserDetails currentUser) {
        Product product;
        if (dto.getId() != null) {
            product = productRepository.findById(dto.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Sản phẩm không tồn tại: " + dto.getId()));

            // Check authorization: must be admin or product owner
            boolean isAdmin = currentUser.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            if (!isAdmin && !product.getUser().getId().equals(currentUser.getId())) {
                throw new AccessDeniedException("Bạn không có quyền chỉnh sửa sản phẩm này!");
            }
        } else {
            product = new Product();
            User user = userRepository.findById(currentUser.getId())
                    .orElseThrow(() -> new IllegalArgumentException("User không tồn tại!"));
            product.setUser(user);
        }

        product.setName(dto.getName());
        product.setBrand(dto.getBrand());
        product.setMadein(dto.getMadein());
        product.setPrice(dto.getPrice());
        product.setDescription(dto.getDescription());

        if (imageFile != null && !imageFile.isEmpty()) {
            String uploadedUrl = cloudinaryService.uploadImage(imageFile, "products");
            if (uploadedUrl != null) {
                product.setImages(uploadedUrl);
            }
        } else if (product.getImages() == null) {
            product.setImages("/images/default-product.png");
        }

        return productMapper.toDto(productRepository.save(product));
    }

    @Override
    @Transactional
    public void deleteProduct(Long id, CustomUserDetails currentUser) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm: " + id));

        boolean isAdmin = currentUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!isAdmin && !product.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("Bạn không có quyền xóa sản phẩm này!");
        }

        productRepository.delete(product);
    }

    @Override
    public long countTotalProducts() {
        return productRepository.countTotalProducts();
    }

    @Override
    public long countProductsByUser(Long userId) {
        return productRepository.countByUserId(userId);
    }
}
