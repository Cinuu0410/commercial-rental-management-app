package com.uwb.commercialrentalmanagementapp.Repository;

import com.uwb.commercialrentalmanagementapp.Model.UtilitiesPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;

public interface UtilitiesPaymentRepository extends JpaRepository<UtilitiesPayment, Long> {


    @Query("SELECT up.status FROM UtilitiesPayment up WHERE up.propertyId = :propertyId")
    String findStatusByPropertyId(Long propertyId);


    @Query("SELECT u.amount FROM UtilitiesPayment u WHERE u.propertyId = :propertyId")
    BigDecimal findAmountByPropertyId(Long propertyId);
}
