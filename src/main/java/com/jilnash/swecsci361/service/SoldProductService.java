package com.jilnash.swecsci361.service;

import com.jilnash.swecsci361.model.SoldProduct;
import com.jilnash.swecsci361.repo.SoldProductRepo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.List;

@Service
public class SoldProductService {

    private final SoldProductRepo soldProductRepo;

    @PersistenceContext
    private EntityManager entityManager;

    public SoldProductService(SoldProductRepo soldProductRepo) {
        this.soldProductRepo = soldProductRepo;
    }

    public List<SoldProduct> getSoldProducts(String farmId, String status, Date startDate, Date endDate) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<SoldProduct> query = cb.createQuery(SoldProduct.class);
        Root<SoldProduct> root = query.from(SoldProduct.class);

        Predicate predicate = cb.conjunction();

        if (farmId != null && !farmId.isEmpty()) {
            predicate = cb.and(predicate, cb.equal(root.get("farmId"), farmId));
        }
        if (status != null && !status.isEmpty()) {
            predicate = cb.and(predicate, cb.equal(root.get("status"), status));
        }
        if (startDate != null) {
            predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("soldDate"), startDate));
        }
        if (endDate != null) {
            predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("soldDate"), endDate));
        }

        query.where(predicate);

        return entityManager.createQuery(query).getResultList();

    }

    public void updateStatus(Long id, String status) {

        soldProductRepo.findById(id).ifPresentOrElse(
                soldProduct -> {
                    soldProduct.setStatus(status);
                    soldProductRepo.save(soldProduct);
                },
                () -> {
                    throw new RuntimeException("Sold product not found");
                }
        );
    }
}
