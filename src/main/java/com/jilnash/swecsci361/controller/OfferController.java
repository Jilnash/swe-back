package com.jilnash.swecsci361.controller;

import com.jilnash.swecsci361.dto.OfferDTO;
import com.jilnash.swecsci361.service.OfferService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/offers")
public class OfferController {

    private final OfferService offerService;

    public OfferController(OfferService offerService) {
        this.offerService = offerService;
    }

    @GetMapping
    public ResponseEntity<?> getOffers(@RequestParam String userId,
                                       @RequestParam Boolean isAccepted,
                                       @RequestParam Long productId) {

        return ResponseEntity.ok(offerService.getOffers(userId, isAccepted, productId));
    }

    @PostMapping
    public ResponseEntity<?> createOffer(@RequestBody OfferDTO offerDTO) {

        return ResponseEntity.ok(offerService.createOffer(offerDTO));
    }

    @PutMapping("/{offerId}")
    public ResponseEntity<?> acceptOffer(@PathVariable Long offerId, @RequestParam Boolean isAccepted) {

        offerService.acceptOffer(offerId, isAccepted);

        return ResponseEntity.ok("Offer status updated successfully");
    }

    @DeleteMapping("/{offerId}")
    public ResponseEntity<?> deleteOffer(@PathVariable Long offerId) {

        offerService.deleteOffer(offerId);

        return ResponseEntity.ok("Offer deleted successfully");
    }
}
