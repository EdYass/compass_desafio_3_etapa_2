package com.EdYass.ecommerce.service;

import com.EdYass.ecommerce.dto.SaleDTO;
import com.EdYass.ecommerce.dto.SaleProductDTO;
import com.EdYass.ecommerce.dto.SaleResponseDTO;
import com.EdYass.ecommerce.entity.Product;
import com.EdYass.ecommerce.entity.Sale;
import com.EdYass.ecommerce.entity.SaleProduct;
import com.EdYass.ecommerce.exception.InsufficientStockException;
import com.EdYass.ecommerce.exception.ProductNotFoundException;
import com.EdYass.ecommerce.exception.SaleNotFoundException;
import com.EdYass.ecommerce.repository.ProductRepository;
import com.EdYass.ecommerce.repository.SaleProductRepository;
import com.EdYass.ecommerce.repository.SaleRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SaleService {
    @Autowired
    private SaleRepository saleRepository;

    @Autowired
    private SaleProductRepository saleProductRepository;

    @Autowired
    private ProductRepository productRepository;

    @Cacheable(value = "sales")
    public List<SaleResponseDTO> getAllSales() {
        return saleRepository.findAll().stream()
                .map(this::convertToSaleResponseDTO)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "sale", key = "#id")
    public SaleResponseDTO getSaleById(Long id) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new SaleNotFoundException("Sale not found"));
        return convertToSaleResponseDTO(sale);
    }

    @Transactional
    @CacheEvict(value = {"sales", "sale"}, allEntries = true)
    public SaleResponseDTO createSale(SaleDTO saleDTO) {
        Sale sale = new Sale();
        sale.setDate(saleDTO.getDate());

        List<SaleProduct> saleProducts = saleDTO.getSaleProducts().stream()
                .map(saleProductDTO -> {
                    Product product = productRepository.findById(saleProductDTO.getProductId())
                            .orElseThrow(() -> new ProductNotFoundException("Product not found"));

                    if (product.getStock() < saleProductDTO.getQuantity()) {
                        throw new InsufficientStockException("Insufficient stock for product: " + product.getName());
                    }

                    product.setStock(product.getStock() - saleProductDTO.getQuantity());
                    productRepository.save(product);

                    SaleProduct saleProduct = new SaleProduct();
                    saleProduct.setSale(sale);
                    saleProduct.setProduct(product);
                    saleProduct.setQuantity(saleProductDTO.getQuantity());
                    saleProduct.setPrice(product.getPrice());
                    return saleProduct;
                })
                .collect(Collectors.toList());

        sale.setSaleProducts(saleProducts);
        Sale savedSale = saleRepository.save(sale);
        return convertToSaleResponseDTO(savedSale);
    }

    @Transactional
    @CacheEvict(value = {"sales", "sale"}, allEntries = true)
    public SaleResponseDTO updateSale(Long id, SaleDTO saleDTO) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new SaleNotFoundException("Sale not found"));

        saleProductRepository.deleteAll(sale.getSaleProducts());

        List<SaleProduct> saleProducts = saleDTO.getSaleProducts().stream()
                .map(saleProductDTO -> {
                    Product product = productRepository.findById(saleProductDTO.getProductId())
                            .orElseThrow(() -> new ProductNotFoundException("Product not found"));

                    if (product.getStock() < saleProductDTO.getQuantity()) {
                        throw new InsufficientStockException("Insufficient stock for product: " + product.getName());
                    }

                    product.setStock(product.getStock() - saleProductDTO.getQuantity());
                    productRepository.save(product);

                    SaleProduct saleProduct = new SaleProduct();
                    saleProduct.setSale(sale);
                    saleProduct.setProduct(product);
                    saleProduct.setQuantity(saleProductDTO.getQuantity());
                    saleProduct.setPrice(product.getPrice());
                    return saleProduct;
                })
                .collect(Collectors.toList());

        sale.setSaleProducts(saleProducts);
        sale.setDate(saleDTO.getDate());
        Sale updatedSale = saleRepository.save(sale);
        return convertToSaleResponseDTO(updatedSale);
    }

    @Transactional
    @CacheEvict(value = {"sales", "sale"}, allEntries = true)
    public void deleteSale(Long id) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new SaleNotFoundException("Sale not found"));

        sale.getSaleProducts().forEach(saleProduct -> {
            Product product = saleProduct.getProduct();
            product.setStock(product.getStock() + saleProduct.getQuantity());
            productRepository.save(product);
        });

        saleRepository.delete(sale);
    }

    private SaleResponseDTO convertToSaleResponseDTO(Sale sale) {
        SaleResponseDTO saleResponseDTO = new SaleResponseDTO();
        saleResponseDTO.setId(sale.getId());
        saleResponseDTO.setDate(sale.getDate());

        List<SaleProductDTO> saleProductDTOs = sale.getSaleProducts().stream()
                .map(saleProduct -> {
                    SaleProductDTO saleProductDTO = new SaleProductDTO();
                    saleProductDTO.setProductId(saleProduct.getProduct().getId());
                    saleProductDTO.setName(saleProduct.getProduct().getName());
                    saleProductDTO.setDescription(saleProduct.getProduct().getDescription());
                    saleProductDTO.setPrice(saleProduct.getPrice());
                    saleProductDTO.setQuantity(saleProduct.getQuantity());
                    return saleProductDTO;
                })
                .collect(Collectors.toList());

        saleResponseDTO.setSaleProducts(saleProductDTOs);

        BigDecimal total = saleProductDTOs.stream()
                .map(dto -> dto.getPrice().multiply(BigDecimal.valueOf(dto.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        saleResponseDTO.setTotal(total);

        return saleResponseDTO;
    }

    public List<SaleResponseDTO> filterSalesByDate(LocalDateTime startDate, LocalDateTime endDate) {
        return saleRepository.findAll().stream()
                .filter(sale -> sale.getDate().isAfter(startDate) && sale.getDate().isBefore(endDate))
                .map(this::convertToSaleResponseDTO)
                .collect(Collectors.toList());
    }

    public List<SaleResponseDTO> getWeeklyReport() {
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minus(1, ChronoUnit.WEEKS);
        return filterSalesByDate(startDate, endDate);
    }

    public List<SaleResponseDTO> getMonthlyReport(int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDateTime startDate = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime endDate = yearMonth.atEndOfMonth().atTime(23, 59, 59);
        return filterSalesByDate(startDate, endDate);
    }
}
