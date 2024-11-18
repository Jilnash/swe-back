package com.jilnash.swecsci361.service;

import com.jilnash.swecsci361.dto.OfferDTO;
import com.jilnash.swecsci361.model.Product;
import com.jilnash.swecsci361.model.ProductOffer;
import com.jilnash.swecsci361.repo.OfferRepo;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class OfferService {

    private final OfferRepo offerRepo;

    public OfferService(OfferRepo offerRepo) {
        this.offerRepo = offerRepo;
    }

    public List<ProductOffer> getOffers(String userId, Boolean isAccepted, Long productId) {

        if (userId != null && isAccepted != null && productId != null) {
            return offerRepo.findAllByUserIdAndIsAccepted(userId, isAccepted);
        } else if (productId != null) {
            return offerRepo.findAllByProduct_IdInAndIsAccepted(List.of(productId), isAccepted);
        } else {
            return offerRepo.findAll();
        }
    }

    public ProductOffer createOffer(OfferDTO offerDTO) {

        return offerRepo.save(
                ProductOffer.builder()
                        .userId(offerDTO.getUserId())
                        .product(Product.builder().id(offerDTO.getProductId()).build())
                        .price(offerDTO.getPrice())
                        .message(offerDTO.getMessage())
                        .expirationDate(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7))
                        .build()
        );
    }

    public void acceptOffer(Long offerId, Boolean isAccepted) {

        ProductOffer offer = offerRepo
                .findById(offerId)
                .orElseThrow(() -> new RuntimeException("Offer not found"));

        offer.setIsAccepted(isAccepted);

        offerRepo.save(offer);
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
                .map(ProductOffer::getPrice);
    }
}
