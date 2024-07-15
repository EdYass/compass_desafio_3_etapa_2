package com.EdYass.ecommerce.controller.role;

import com.EdYass.ecommerce.dto.ProductDTO;
import com.EdYass.ecommerce.dto.SaleDTO;
import com.EdYass.ecommerce.dto.SaleResponseDTO;
import com.EdYass.ecommerce.entity.User;
import com.EdYass.ecommerce.service.ProductService;
import com.EdYass.ecommerce.service.SaleService;
import com.EdYass.ecommerce.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final ProductService productService;
    private final SaleService saleService;
    private final UserService userService;

    @Autowired
    public AdminController(ProductService productService, SaleService saleService, UserService userService) {
        this.productService = productService;
        this.saleService = saleService;
        this.userService = userService;
    }

    @PostMapping("/products")
    public ResponseEntity<?> createProduct(@RequestBody ProductDTO productDTO) {
        productService.createProduct(productDTO);
        return ResponseEntity.ok("Product created successfully");
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @RequestBody ProductDTO productDTO) {
        productService.updateProduct(id, productDTO);
        return ResponseEntity.ok("Product updated successfully");
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/sales")
    public ResponseEntity<?> createSale(@RequestBody SaleDTO saleDTO) {
        SaleResponseDTO responseDTO = saleService.createSale(saleDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @PutMapping("/sales/{id}")
    public ResponseEntity<?> updateSale(@PathVariable Long id, @RequestBody SaleDTO saleDTO) {
        SaleResponseDTO responseDTO = saleService.updateSale(id, saleDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("/sales/{id}")
    public ResponseEntity<Void> deleteSale(@PathVariable Long id) {
        saleService.deleteSale(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/users/{id}")
    public User getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}