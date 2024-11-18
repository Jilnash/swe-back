package com.jilnash.swecsci361.controller;

import com.jilnash.swecsci361.dto.OfferDTO;
import com.jilnash.swecsci361.dto.User;
import com.jilnash.swecsci361.service.OfferService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/offers")
public class OfferController {

    private final OfferService offerService;

    private final HttpServletRequest request;

    public OfferController(OfferService offerService, HttpServletRequest request) {
        this.offerService = offerService;
        this.request = request;
    }

    @GetMapping
    public ResponseEntity<?> getOffers(@RequestParam String userId,
                                       @RequestParam Boolean isAccepted,
                                       @RequestParam Long productId) {

        return ResponseEntity.ok(offerService.getOffers(userId, isAccepted, productId));
    }

    @PostMapping
    public ResponseEntity<?> createOffer(@RequestBody OfferDTO offerDTO) {

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + request.getHeader("Authorization").substring(7));

        HttpEntity<String> entity = new HttpEntity<>(headers);

        offerDTO.setUserId(
                new RestTemplate().exchange(
                        "http://92.46.74.132:8888/user/me", HttpMethod.GET, entity, User.class
                ).getBody().getUuid()
        );

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
