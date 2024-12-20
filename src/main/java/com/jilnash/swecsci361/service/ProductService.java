package com.jilnash.swecsci361.service;

import com.jilnash.swecsci361.dto.*;
import com.jilnash.swecsci361.model.Product;
import com.jilnash.swecsci361.repo.ProductRepo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.net.URL;
import java.sql.Date;
import java.util.List;

@Service
public class ProductService {

    private final ProductRepo productRepo;

    private final S3Service s3Service;

    private final OfferService offerService;

    @PersistenceContext
    private EntityManager entityManager;

    public ProductService(ProductRepo productRepo, S3Service s3Service, OfferService offerService) {
        this.productRepo = productRepo;
        this.s3Service = s3Service;
        this.offerService = offerService;
    }

    public List<ProductListItemDTO> getProducts(String sort, String order, Filter filter) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Product> query = cb.createQuery(Product.class);
        Root<Product> product = query.from(Product.class);

        Predicate predicate = cb.conjunction();

        if (filter.getName() != null) {
            predicate = cb.and(predicate, cb.like(product.get("name"), "%" + filter.getName() + "%"));
        }
        if (filter.getCategory() != null) {
            predicate = cb.and(predicate, cb.equal(product.get("category"), filter.getCategory()));
        }
        if (filter.getFarmId() != null) {
            predicate = cb.and(predicate, cb.equal(product.get("farmId"), filter.getFarmId()));
        }
        if (filter.getMinPrice() != null) {
            predicate = cb.and(predicate, cb.greaterThanOrEqualTo(product.get("price"), filter.getMinPrice()));
        }
        if (filter.getMaxPrice() != null) {
            predicate = cb.and(predicate, cb.lessThanOrEqualTo(product.get("price"), filter.getMaxPrice()));
        }

        query.where(predicate);

        if ("asc".equalsIgnoreCase(order)) {
            query.orderBy(cb.asc(product.get(sort)));
        } else {
            query.orderBy(cb.desc(product.get(sort)));
        }

        return entityManager.createQuery(query).getResultList().stream()
                .map(p ->
                        ProductListItemDTO.builder()
                                .id(p.getId())
                                .name(p.getName())
                                .category(p.getCategory())
                                .price(p.getPrice())
                                .unit(p.getUnit())
                                .quantity(p.getQuantity())
                                .description(p.getDescription())
                                .farmId(p.getFarmId())
                                .farmName(p.getFarmName())
                                .imageURL(
                                        s3Service.getFileURL(
                                                "product-" + p.getId(),
                                                s3Service.getFirstFileName("product-" + p.getId())
                                        ).toString())
                                .build()
                ).toList();
    }

    public Product getProduct(Long id) {
        return productRepo.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
    }

    public ProductResponseDTO getProductDTO(Long id) {
        return productRepo.findById(id)
                .map(p -> ProductResponseDTO.builder()
                        .id(p.getId())
                        .name(p.getName())
                        .category(p.getCategory())
                        .price(p.getPrice())
                        .unit(p.getUnit())
                        .quantity(p.getQuantity())
                        .description(p.getDescription())
                        .farmId(p.getFarmId())
                        .farmName(p.getFarmName())
                        .imageUrls(
                                s3Service.getFileURLs("product-" + p.getId()).stream()
                                        .map(URL::toString)
                                        .toList()
                        )
                        .build()
                )
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    public Boolean createProduct(List<MultipartFile> images, ProductCreateDTO productDTO) {

        //upload images
        if (images == null || images.isEmpty()) {
            throw new RuntimeException("Images are required");
        }

        Long id = productRepo.save(
                Product.builder()
                        .name(productDTO.getName())
                        .category(productDTO.getCategory())
                        .price(productDTO.getPrice())
                        .unit(productDTO.getUnit())
                        .quantity(productDTO.getQuantity())
                        .description(productDTO.getDescription())
                        .farmId(productDTO.getFarmId())
                        .build()
        ).getId();

        try {
            s3Service.createBucket("product-" + id);
            s3Service.putFiles(images, "product-" + id);
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload image");
        }

        return true;
    }

    public Product updateProduct(ProductUpdateDTO productDTO) {

        Date createdAt = productRepo.findById(
                productDTO.getId()).orElseThrow(() -> new RuntimeException("Product not found")
        ).getCreatedAt();

        return productRepo.save(
                Product.builder()
                        .id(productDTO.getId())
                        .name(productDTO.getName())
                        .category(productDTO.getCategory())
                        .price(productDTO.getPrice())
                        .unit(productDTO.getUnit())
                        .quantity(productDTO.getQuantity())
                        .description(productDTO.getDescription())
                        .createdAt(createdAt)
                        .updatedAt(new Date(System.currentTimeMillis()))
                        .build()
        );
    }

    public List<URL> getProductImages(Long id) {
        return s3Service.getFileURLs("product-" + id);
    }

    public void updateProductImages(Long id, List<MultipartFile> images) {

        if (images == null || images.isEmpty()) {
            throw new RuntimeException("Images are required");
        }

        try {
            s3Service.deleteFiles("product-" + id);
            s3Service.putFiles(images, "product-" + id);
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload images");
        }
    }

    public void deleteProduct(Long id) {
        productRepo.deleteById(id);
        s3Service.deleteFiles("product-" + id);
        s3Service.deleteBucket("product-" + id);
    }

    public Boolean checkAvailability(Long id, Double quantity) {
        return productRepo.existsByIdAndQuantityGreaterThan(id, quantity);
    }

    public void reduceQuantity(Long id, Double quantity) {
        productRepo.reduceQuantity(id, quantity);
    }

    public Double getProductPriceForUser(Long id, String userId) {

        //get offer price if exists
        //else get product price
        return offerService.getOfferPrice(id, userId).orElseGet(() -> productRepo.getPriceById(id));
    }
}
