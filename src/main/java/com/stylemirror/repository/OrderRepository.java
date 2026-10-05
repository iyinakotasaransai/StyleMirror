package com.stylemirror.repository;

import com.stylemirror.entity.Order;
import com.stylemirror.entity.User;
import com.stylemirror.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUserOrderByCreatedAtDesc(User user);

    Page<Order> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);

    Optional<Order> findByOrderNumber(String orderNumber);

    long countByStatus(OrderStatus status);

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.status != 'CANCELLED'")
    BigDecimal calculateTotalRevenue();

    @Query("SELECT DISTINCT o FROM Order o JOIN o.items i JOIN i.productVariant pv JOIN pv.product p WHERE p.seller.id = :sellerId ORDER BY o.createdAt DESC")
    List<Order> findOrdersBySellerId(@Param("sellerId") Long sellerId);

    @Query("SELECT DISTINCT o FROM Order o JOIN o.items i JOIN i.productVariant pv JOIN pv.product p WHERE p.seller.id = :sellerId ORDER BY o.createdAt DESC")
    Page<Order> findOrdersBySellerId(@Param("sellerId") Long sellerId, Pageable pageable);

    @Query("SELECT COALESCE(SUM(i.subtotal), 0) FROM Order o JOIN o.items i JOIN i.productVariant pv JOIN pv.product p WHERE p.seller.id = :sellerId AND o.status != 'CANCELLED'")
    BigDecimal calculateTotalSalesBySellerId(@Param("sellerId") Long sellerId);

    @Query("SELECT COUNT(DISTINCT o) FROM Order o JOIN o.items i JOIN i.productVariant pv JOIN pv.product p WHERE p.seller.id = :sellerId AND o.status = 'PROCESSING'")
    long countPendingOrdersBySellerId(@Param("sellerId") Long sellerId);
}
