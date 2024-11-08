package com.jilnash.swecsci361.service;

import com.jilnash.swecsci361.dto.OfferDTO;
import com.jilnash.swecsci361.model.ProductOffer;
import com.jilnash.swecsci361.repo.OfferRepo;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class OfferService {

    private final OfferRepo offerRepo;

    private final ProductService productService;

    public OfferService(OfferRepo offerRepo, ProductService productService) {
        this.offerRepo = offerRepo;
        this.productService = productService;
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

        return ProductOffer.builder()
                .userId(offerDTO.getUserId())
                .product(productService.getProduct(offerDTO.getProductId()))
                .price(offerDTO.getPrice())
                .message(offerDTO.getMessage())
                .expirationDate(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7))
                .build();
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
}
