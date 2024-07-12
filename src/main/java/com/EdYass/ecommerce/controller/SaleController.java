package com.EdYass.ecommerce.controller;

import com.EdYass.ecommerce.dto.SaleDTO;
import com.EdYass.ecommerce.dto.SaleResponseDTO;
import com.EdYass.ecommerce.service.SaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/sales")
public class SaleController {
    @Autowired
    private SaleService saleService;

    @GetMapping
    public List<SaleResponseDTO> getAllSales() {
        return saleService.getAllSales();
    }

    @GetMapping("/{id}")
    public SaleResponseDTO getSaleById(@PathVariable Long id) {
        return saleService.getSaleById(id);
    }

    @PostMapping
    public SaleResponseDTO createSale(@RequestBody SaleDTO saleDTO) {
        return saleService.createSale(saleDTO);
    }

    @PutMapping("/{id}")
    public SaleResponseDTO updateSale(@PathVariable Long id, @RequestBody SaleDTO saleDTO) {
        return saleService.updateSale(id, saleDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSale(@PathVariable Long id) {
        saleService.deleteSale(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/filter")
    public List<SaleResponseDTO> filterSalesByDate(@RequestParam LocalDateTime startDate, @RequestParam LocalDateTime endDate) {
        return saleService.filterSalesByDate(startDate, endDate);
    }

    @GetMapping("/report/weekly")
    public List<SaleResponseDTO> getWeeklyReport() {
        return saleService.getWeeklyReport();
    }

    @GetMapping("/report/monthly")
    public List<SaleResponseDTO> getMonthlyReport(@RequestParam int year, @RequestParam int month) {
        return saleService.getMonthlyReport(year, month);
    }
}
