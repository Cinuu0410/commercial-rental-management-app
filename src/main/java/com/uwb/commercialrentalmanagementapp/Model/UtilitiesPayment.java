package com.uwb.commercialrentalmanagementapp.Model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Entity
@Table(name = "utilities_payments")
public class UtilitiesPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long paymentId;

    @Column(name = "payment_date")
    private Date paymentDate;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "property_id")
    private Long propertyId;

    @Column(name = "status")
    private String status;

    @Column(name = "payment_month")
    private String paymentMonth;


    public UtilitiesPayment() {

    }
}
