package com.EdYass.ecommerce.service;

import com.EdYass.ecommerce.dto.ProductDTO;
import com.EdYass.ecommerce.entity.Product;
import com.EdYass.ecommerce.exception.ProductInUseException;
import com.EdYass.ecommerce.exception.ProductNotFoundException;
import com.EdYass.ecommerce.repository.ProductRepository;
import com.EdYass.ecommerce.repository.SaleProductRepository;
import com.EdYass.ecommerce.security.CheckPermission;
import jakarta.transaction.Transactional;
import org.springframework.context.ApplicationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    private final SaleProductRepository saleProductRepository;

    private final CheckPermission checkPermission;

    private final ApplicationContext applicationContext;

    @Autowired
    public ProductService(ProductRepository productRepository, SaleProductRepository saleProductRepository, CheckPermission checkPermission, ApplicationContext applicationContext) {
        this.productRepository = productRepository;
        this.saleProductRepository = saleProductRepository;
        this.checkPermission = checkPermission;
        this.applicationContext = applicationContext;
    }

    @Cacheable("products")
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Cacheable(value = "product", key = "#id")
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));
    }

    @Transactional
    @CacheEvict(value = {"products", "product"}, allEntries = true)
    public Product createProduct(ProductDTO productDTO) {
        checkPermission.Permission("ADMIN", "SELLER");
        Product product = new Product();
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        product.setStock(productDTO.getStock());
        return productRepository.save(product);
    }

    @Transactional
    @CacheEvict(value = {"products", "product"}, allEntries = true)
    public Product updateProduct(Long id, ProductDTO productDTO) {
        checkPermission.Permission("ADMIN", "SELLER");
        Product product = getProductServiceProxy().getProductById(id);
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        product.setStock(productDTO.getStock());
        return productRepository.save(product);
    }

    @Transactional
    @CacheEvict(value = {"products", "product"}, allEntries = true)
    public void deleteProduct(Long id) {
        checkPermission.Permission("ADMIN");
        Product product = getProductServiceProxy().getProductById(id);
        if (saleProductRepository.existsByProductId(id)) {
            throw new ProductInUseException("Product is already included in a sale and cannot be deleted.");
        } else {
            productRepository.delete(product);
        }
    }

    private ProductService getProductServiceProxy() {
        return applicationContext.getBean(ProductService.class);
    }
}
