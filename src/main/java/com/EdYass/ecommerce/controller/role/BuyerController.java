package com.EdYass.ecommerce.controller.role;

import com.EdYass.ecommerce.dto.SaleDTO;
import com.EdYass.ecommerce.dto.SaleResponseDTO;
import com.EdYass.ecommerce.service.SaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/buyer")
public class BuyerController {

    private final SaleService saleService;

    @Autowired
    public BuyerController(SaleService saleService) {
        this.saleService = saleService;
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

    @GetMapping("/sales/{id}")
    public ResponseEntity<SaleResponseDTO> getSaleById(@PathVariable Long id) {
        SaleResponseDTO responseDTO = saleService.getSaleById(id);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/sales")
    public ResponseEntity<List<SaleResponseDTO>> getAllSales() {
        List<SaleResponseDTO> responseDTOs = saleService.getAllSales();
        return ResponseEntity.ok(responseDTOs);
    }
}
