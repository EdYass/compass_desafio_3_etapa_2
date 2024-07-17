package com.EdYass.ecommerce.service;

import com.EdYass.ecommerce.dto.SaleDTO;
import com.EdYass.ecommerce.dto.SaleProductDTO;
import com.EdYass.ecommerce.dto.SaleResponseDTO;
import com.EdYass.ecommerce.entity.Product;
import com.EdYass.ecommerce.entity.Sale;
import com.EdYass.ecommerce.entity.SaleProduct;
import com.EdYass.ecommerce.entity.User;
import com.EdYass.ecommerce.exception.InsufficientStockException;
import com.EdYass.ecommerce.exception.ProductNotFoundException;
import com.EdYass.ecommerce.exception.SaleNotFoundException;
import com.EdYass.ecommerce.exception.UserNotFoundException;
import com.EdYass.ecommerce.repository.ProductRepository;
import com.EdYass.ecommerce.repository.SaleProductRepository;
import com.EdYass.ecommerce.repository.SaleRepository;
import com.EdYass.ecommerce.repository.UserRepository;
import com.EdYass.ecommerce.security.CheckPermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SaleService {

    private final SaleRepository saleRepository;
    private final SaleProductRepository saleProductRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CheckPermission checkPermission;

    @Autowired
    public SaleService(ProductRepository productRepository, SaleRepository saleRepository, SaleProductRepository saleProductRepository, UserRepository userRepository, CheckPermission checkPermission) {
        this.productRepository = productRepository;
        this.saleRepository = saleRepository;
        this.saleProductRepository = saleProductRepository;
        this.userRepository = userRepository;
        this.checkPermission = checkPermission;
    }

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
    public SaleResponseDTO createSale(SaleDTO saleDTO, String email) {
        checkPermission.Permission("ADMIN", "BUYER");
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Sale sale = new Sale();
        sale.setUser(user);
        sale.setDate(saleDTO.getDate());

        List<SaleProduct> saleProducts = processSaleProducts(saleDTO, sale);
        sale.setSaleProducts(saleProducts);
        Sale savedSale = saleRepository.save(sale);
        return convertToSaleResponseDTO(savedSale);
    }

    @Transactional
    @CacheEvict(value = {"sales", "sale"}, allEntries = true)
    public SaleResponseDTO updateSale(Long id, SaleDTO saleDTO, String email) {
        checkPermission.Permission("ADMIN", "BUYER");
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new SaleNotFoundException("Sale not found"));

        saleProductRepository.deleteAll(sale.getSaleProducts());

        List<SaleProduct> saleProducts = processSaleProducts(saleDTO, sale);
        sale.setSaleProducts(saleProducts);
        sale.setDate(saleDTO.getDate());
        sale.setUser(user);
        Sale updatedSale = saleRepository.save(sale);
        return convertToSaleResponseDTO(updatedSale);
    }

    @Transactional
    @CacheEvict(value = {"sales", "sale"}, allEntries = true)
    public void deleteSale(Long id) {
        checkPermission.Permission("ADMIN");
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new SaleNotFoundException("Sale not found"));

        sale.getSaleProducts().forEach(saleProduct -> {
            Product product = saleProduct.getProduct();
            product.setStock(product.getStock() + saleProduct.getQuantity());
            productRepository.save(product);
        });

        saleRepository.delete(sale);
    }

    private List<SaleProduct> processSaleProducts(SaleDTO saleDTO, Sale sale) {
        return saleDTO.getSaleProducts().stream()
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
    }

    private SaleResponseDTO convertToSaleResponseDTO(Sale sale) {
        SaleResponseDTO saleResponseDTO = new SaleResponseDTO();
        saleResponseDTO.setId(sale.getId());
        saleResponseDTO.setDate(sale.getDate());
        saleResponseDTO.setUserEmail(sale.getUser().getEmail());

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
        checkPermission.Permission("ADMIN");
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusWeeks(1);
        return filterSalesByDate(startDate, endDate);
    }

    public List<SaleResponseDTO> getMonthlyReport(int year, int month) {
        checkPermission.Permission("ADMIN");
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDateTime startDate = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime endDate = yearMonth.atEndOfMonth().atTime(23, 59, 59);
        return filterSalesByDate(startDate, endDate);
    }
}
