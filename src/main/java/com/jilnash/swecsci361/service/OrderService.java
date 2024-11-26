package com.jilnash.swecsci361.service;

import com.jilnash.swecsci361.dto.OrderDTO;
import com.jilnash.swecsci361.model.Order;
import com.jilnash.swecsci361.model.Product;
import com.jilnash.swecsci361.model.SoldProduct;
import com.jilnash.swecsci361.repo.OrderRepo;
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
public class OrderService {

    private final OrderRepo orderRepo;

    private final ProductService productService;

    private final SoldProductRepo soldProductRepo;

    @PersistenceContext
    private EntityManager entityManager;

    public OrderService(OrderRepo orderRepo, ProductService productService, SoldProductRepo soldProductRepo) {
        this.orderRepo = orderRepo;
        this.productService = productService;
        this.soldProductRepo = soldProductRepo;
    }

    public List<Order> getOrders(String buyerId, String status, Date createdAfter, Date createdBefore) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Order> query = cb.createQuery(Order.class);
        Root<Order> order = query.from(Order.class);

        Predicate predicate = cb.conjunction();

        if (buyerId != null)
            predicate = cb.and(predicate, cb.equal(order.get("buyerId"), buyerId));

        if (status != null)
            predicate = cb.and(predicate, cb.equal(order.get("status"), status));

        if (createdAfter != null)
            predicate = cb.and(predicate, cb.greaterThanOrEqualTo(order.get("createdDate"), createdAfter));

        if (createdBefore != null)
            predicate = cb.and(predicate, cb.lessThanOrEqualTo(order.get("createdDate"), createdBefore));

        query.where(predicate);

        return entityManager.createQuery(query).getResultList();
    }

    public void createOrder(OrderDTO orderDto) {

        //check if products are available
        orderDto.getProducts().forEach(orderProductDto -> {

            if (!productService.checkAvailability(orderProductDto.getProductId(), orderProductDto.getQuantity()))
                throw new RuntimeException("Product does not exist or quantity exceeded");
        });

        // Setting selling price for each product
        orderDto.getProducts().forEach(orderProductDto ->
                orderProductDto.setSoldPrice(
                        productService.getProductPriceForUser(
                                orderProductDto.getProductId(),
                                orderDto.getBuyerId()
                        )
                )
        );

        // Create order
        Order order = new Order();

        order.setBuyerId(orderDto.getBuyerId());
        order.setTotalPrice(
                orderDto.getProducts().stream()
                        .map(productDto -> productDto.getSoldPrice() * productDto.getQuantity())
                        .reduce(0.0, Double::sum)
        );

        // Save order
        Order finalOrder = orderRepo.save(order);

        // Update product quantity and save sold product
        orderDto.getProducts().forEach(orderProductDto -> {

            // Reduce product quantity then get the product
            productService.reduceQuantity(orderProductDto.getProductId(), orderProductDto.getQuantity());
            Product product = productService.getProduct(orderProductDto.getProductId());

            // Create then save sold product
            soldProductRepo.save(
                    SoldProduct.builder()
                            .name(product.getName())
                            .category(product.getCategory())
                            .soldPrice(orderProductDto.getSoldPrice())
                            .quantity(orderProductDto.getQuantity())
                            .status("ORDERED")
                            .order(finalOrder)
                            .build());
        });
    }
}
