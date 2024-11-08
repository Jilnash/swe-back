package com.jilnash.swecsci361.repo;

import com.jilnash.swecsci361.model.ProductOffer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OfferRepo extends JpaRepository<ProductOffer, Long> {

    List<ProductOffer> findAllByUserIdAndIsAccepted(String userId, Boolean isAccepted);

    List<ProductOffer> findAllByProduct_IdInAndIsAccepted(List<Long> productIds, Boolean isAccepted);
}
