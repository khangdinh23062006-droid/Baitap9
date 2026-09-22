package vn.iotstar.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.iotstar.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("""
        SELECT p FROM Product p
        WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :kw, '%'))
           OR LOWER(p.brand) LIKE LOWER(CONCAT('%', :kw, '%'))
           OR LOWER(p.madein) LIKE LOWER(CONCAT('%', :kw, '%'))
    """)
    Page<Product> searchProducts(@Param("kw") String keyword, Pageable pageable);

    Page<Product> findByUserId(Long userId, Pageable pageable);

    @Query("SELECT COUNT(p) FROM Product p WHERE p.user.id = :userId")
    long countByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(p) FROM Product p")
    long countTotalProducts();
}
