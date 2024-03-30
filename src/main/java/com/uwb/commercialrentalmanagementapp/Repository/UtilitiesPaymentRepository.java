package com.uwb.commercialrentalmanagementapp.Repository;

import com.uwb.commercialrentalmanagementapp.Model.UtilitiesPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface UtilitiesPaymentRepository extends JpaRepository<UtilitiesPayment, Long> {


    @Query("SELECT up.status FROM UtilitiesPayment up WHERE up.propertyId = :propertyId")
    String findStatusByPropertyId(Long propertyId);


    @Query("SELECT u.amount FROM UtilitiesPayment u WHERE u.propertyId = :propertyId")
    List<BigDecimal> findAmountsByPropertyId(Long propertyId);

    @Query("SELECT u.paymentMonth FROM UtilitiesPayment u WHERE u.propertyId = :propertyId")
    List<String> findMonthsByPropertyId(Long propertyId);


    @Query("SELECT u.amount FROM UtilitiesPayment u WHERE u.propertyId = :propertyId AND u.paymentId = (SELECT MAX(p.paymentId) FROM UtilitiesPayment p WHERE p.propertyId = :propertyId)")
    BigDecimal findLatestUtilitiesAmountForProperty(@Param("propertyId") Long propertyId);

}
