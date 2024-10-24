package com.hys.exam.springbatch_app_10.app.order.repository;

import com.hys.exam.springbatch_app_10.app.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
