package com.sesac.backend.payments.repository;

import com.sesac.backend.orders.domain.Order;
import com.sesac.backend.payments.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByImpUid(String impUid);
    Optional<Payment> findByOrder(Order order);

}
