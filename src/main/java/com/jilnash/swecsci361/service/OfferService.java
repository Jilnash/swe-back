package com.jilnash.swecsci361.service;

import com.jilnash.swecsci361.dto.OfferDTO;
import com.jilnash.swecsci361.model.Product;
import com.jilnash.swecsci361.model.ProductOffer;
import com.jilnash.swecsci361.repo.OfferRepo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class OfferService {

    private final OfferRepo offerRepo;

    @PersistenceContext
    private final EntityManager entityManager;

    public OfferService(OfferRepo offerRepo, EntityManager entityManager) {
        this.offerRepo = offerRepo;
        this.entityManager = entityManager;
    }

    public List<ProductOffer> getOffers(String userId, Boolean isAccepted, Long productId, Boolean isExpired, String farmerId) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<ProductOffer> query = cb.createQuery(ProductOffer.class);
        Root<ProductOffer> order = query.from(ProductOffer.class);

        Predicate predicate = cb.conjunction();

        if (userId != null)
            predicate = cb.and(predicate, cb.equal(order.get("userId"), userId));

        if (isAccepted != null)
            predicate = cb.and(predicate, cb.equal(order.get("isAccepted"), isAccepted));

        if (productId != null)
            predicate = cb.and(predicate, cb.equal(order.get("product").get("id"), productId));

        if (farmerId != null)
            predicate = cb.and(predicate, cb.equal(order.get("product").get("farmId"), farmerId));

        if (isExpired != null)
            if (isExpired)
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(order.get("expirationDate"), new Date()));
            else
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(order.get("expirationDate"), new Date()));

        query.where(predicate);

        return entityManager.createQuery(query).getResultList();
    }

    public ProductOffer createOffer(OfferDTO offerDTO) {

        return offerRepo.save(
                ProductOffer.builder()
                        .userId(offerDTO.getUserId())
                        .buyerName(offerDTO.getBuyerName())
                        .product(Product.builder().id(offerDTO.getProductId()).build())
                        .price(offerDTO.getPrice())
                        .message(offerDTO.getMessage())
                        .expirationDate(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7))
                        .build()
        );
    }

    public void acceptOffer(Long offerId, Boolean isAccepted) {

        offerRepo.findById(offerId).ifPresentOrElse(
                offer -> {
                    if (isAccepted) {
                        offer.setIsAccepted(true);
                        offerRepo.save(offer);
                    } else {
                        offerRepo.delete(offer);
                    }
                },
                () -> {
                    throw new RuntimeException("Offer not found");
                }
        );
    }

    public void deleteOffer(Long offerId) {

        offerRepo.delete(
                offerRepo.findById(offerId).orElseThrow(() -> new RuntimeException("Offer not found"))
        );
    }

    public Optional<Double> getOfferPrice(Long productId, String userId) {

        return offerRepo
                .getProductOfferByUserIdAndProduct_IdAndIsAcceptedAndExpirationDateGreaterThan
                        (userId, productId, true, new Date())
                .map(offer -> {
                    offerRepo.delete(offer);
                    return offer.getPrice();
                });
    }
}
