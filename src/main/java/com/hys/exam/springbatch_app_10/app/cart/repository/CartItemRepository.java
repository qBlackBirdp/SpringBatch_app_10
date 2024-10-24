package com.hys.exam.springbatch_app_10.app.cart.repository;

import com.hys.exam.springbatch_app_10.app.cart.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByBuyerIdAndProductId(long buyerId, long productId);
    boolean existsByBuyerIdAndProductId(long buyerId, long productId);
}
