package com.jilnash.swecsci361.service;

import com.jilnash.swecsci361.dto.ProductCreateDTO;
import com.jilnash.swecsci361.dto.ProductUpdateDTO;
import com.jilnash.swecsci361.model.Product;
import com.jilnash.swecsci361.repo.ProductRepo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Date;
import java.util.List;

@Service
public class ProductService {

    private final ProductRepo productRepo;

    private final S3Service s3Service;

    @PersistenceContext
    private EntityManager entityManager;

    public ProductService(ProductRepo productRepo, S3Service s3Service) {
        this.productRepo = productRepo;
        this.s3Service = s3Service;
    }

    public List<Product> getProducts(String sort, String order) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Product> query = cb.createQuery(Product.class);
        Root<Product> product = query.from(Product.class);
//
//        Predicate predicate = cb.conjunction();
//
//        if (filter.getName() != null) {
//            predicate = cb.and(predicate, cb.like(product.get("name"), "%" + filter.getName() + "%"));
//        }
//        if (filter.getCategory() != null) {
//            predicate = cb.and(predicate, cb.equal(product.get("category"), filter.getCategory()));
//        }
//        if (filter.getMinPrice() != null) {
//            predicate = cb.and(predicate, cb.greaterThanOrEqualTo(product.get("price"), filter.getMinPrice()));
//        }
//        if (filter.getMaxPrice() != null) {
//            predicate = cb.and(predicate, cb.lessThanOrEqualTo(product.get("price"), filter.getMaxPrice()));
//        }
//
//        query.where(predicate);

        if ("asc".equalsIgnoreCase(order)) {
            query.orderBy(cb.asc(product.get(sort)));
        } else {
            query.orderBy(cb.desc(product.get(sort)));
        }

        return entityManager.createQuery(query).getResultList();
    }

    public Product getProduct(Long id) {
        return productRepo.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
    }

    public List<String> getProductImagesNames(Long id) {
        return s3Service.getFilesNames("product-" + id);
    }

    public Resource getProductImage(Long id, String fileName) {

        return s3Service.getFile("product-" + id, fileName);
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
}
