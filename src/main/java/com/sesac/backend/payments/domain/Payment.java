package com.sesac.backend.payments.domain;

import com.sesac.backend.audit.BaseEntity;
import com.sesac.backend.orders.domain.Order;
import com.sesac.backend.payments.constants.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Payment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orderId", unique = true)
    private Order order;

    private String impUid;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    private String method;

    private Integer amount;

    private LocalDateTime paidAt;

    private LocalDateTime refundedAt;

    private Integer refundAmount;

}
