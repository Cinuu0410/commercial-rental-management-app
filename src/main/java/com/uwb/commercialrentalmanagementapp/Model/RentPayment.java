package com.uwb.commercialrentalmanagementapp.Model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Entity
@Table(name = "rent_payments")
public class RentPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long paymentId;

    @Column(name = "payment_date")
    private Date paymentDate;

    @Column(name = "rental_agreement_id")
    private Long rentalAgreementId;

    @Column(name = "status")
    private String status;

    @Column(name = "amount")
    private BigDecimal paymentAmount;

    @Column(name = "vat_paid")
    private Boolean vatPaid;

    public RentPayment() {}
}
