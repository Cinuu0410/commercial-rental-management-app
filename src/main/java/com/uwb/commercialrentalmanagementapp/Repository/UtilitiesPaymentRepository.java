package com.uwb.commercialrentalmanagementapp.Repository;

import com.uwb.commercialrentalmanagementapp.Model.UtilitiesPayment;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface UtilitiesPaymentRepository extends JpaRepository<UtilitiesPayment, Long> {


    @Query("SELECT up.status FROM UtilitiesPayment up WHERE up.propertyId = :propertyId")
    String findStatusByPropertyId(Long propertyId);


    @Query("SELECT u.amount FROM UtilitiesPayment u WHERE u.propertyId = :propertyId")
    List<BigDecimal> findAmountsByPropertyId(Long propertyId);

    @Query("SELECT u.paymentMonth FROM UtilitiesPayment u WHERE u.propertyId = :propertyId")
    List<String> findMonthsByPropertyId(Long propertyId);


    @Query("SELECT u.amount FROM UtilitiesPayment u WHERE u.propertyId = :propertyId AND u.paymentId = (SELECT MAX(p.paymentId) FROM UtilitiesPayment p WHERE p.propertyId = :propertyId)")
    BigDecimal findLatestUtilitiesAmountForProperty(@Param("propertyId") Long propertyId);

    @Query("SELECT u.paymentMonth FROM UtilitiesPayment u WHERE u.propertyId = :propertyId AND u.status = 'unpaid'")
    String findUnpaidPaymentMonthByPropertyId(Long propertyId);

    @Query("SELECT u.paymentId FROM UtilitiesPayment u WHERE u.propertyId = :propertyId AND u.status = 'unpaid'")
    Long findPaymentIdForUnpaidPaymentByPropertyId(Long propertyId);

    @Modifying
    @Transactional
    @Query("UPDATE UtilitiesPayment up SET up.status = 'paid', up.paymentDate = :paymentDate WHERE up.paymentId = :paymentId AND up.status = 'unpaid'")
    void updateStatusToPaid(Long paymentId, Date paymentDate);
}