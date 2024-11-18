package com.jilnash.swecsci361.repo;

import com.jilnash.swecsci361.model.ProductOffer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface OfferRepo extends JpaRepository<ProductOffer, Long> {

    List<ProductOffer> findAllByUserIdAndIsAccepted(String userId, Boolean isAccepted);

    List<ProductOffer> findAllByProduct_IdInAndIsAccepted(List<Long> productIds, Boolean isAccepted);

    Optional<ProductOffer> getProductOfferByUserIdAndProduct_IdAndIsAcceptedAndExpirationDateGreaterThan(String userId, Long product_id, Boolean isAccepted, Date expirationDate);
}
