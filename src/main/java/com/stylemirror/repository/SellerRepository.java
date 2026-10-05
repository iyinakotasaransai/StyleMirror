package com.stylemirror.repository;

import com.stylemirror.entity.Seller;
import com.stylemirror.entity.User;
import com.stylemirror.enums.ApprovalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SellerRepository extends JpaRepository<Seller, Long> {
    Optional<Seller> findByUser(User user);
    Optional<Seller> findByUserId(Long userId);
    List<Seller> findByApprovalStatus(ApprovalStatus approvalStatus);
    long countByApprovalStatus(ApprovalStatus approvalStatus);
}
