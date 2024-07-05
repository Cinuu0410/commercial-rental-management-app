package com.uwb.commercialrentalmanagementapp.Repository;

import com.uwb.commercialrentalmanagementapp.Model.RentPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface RentPaymentRepository extends JpaRepository<RentPayment, Long> {

    Optional<RentPayment> findByRentalAgreementId(Long rentalAgreementId);

    @Query("SELECT rp.issueDate FROM RentPayment rp WHERE rp.rentalAgreementId = :rentalAgreementId AND rp.paymentId = (SELECT MAX(rp2.paymentId) FROM RentPayment rp2 WHERE rp2.rentalAgreementId = :rentalAgreementId AND rp2.status = 'paid')")
    Optional<Date> findLatestPaymentDateByRentalAgreementId(@Param("rentalAgreementId") Long rentalAgreementId);

    List<RentPayment> findAllByRentalAgreementId(Long rentalAgreementId);

    Optional<RentPayment> findByPaymentId(Long paymentId);


    @Query("SELECT rp FROM RentPayment rp WHERE rp.rentalAgreementId = :rentalAgreementId ORDER BY rp.issueDate DESC")
    List<RentPayment> findLastRentInfoByRentalAgreementId(@Param("rentalAgreementId") Long rentalAgreementId);


    @Query("SELECT rp FROM RentPayment rp JOIN RentalAgreement ra ON rp.rentalAgreementId = ra.agreementId JOIN Property p ON ra.property.propertyId = p.propertyId WHERE p.propertyId = :propertyId")
    List<RentPayment> findAllByPropertyId(@Param("propertyId") Long propertyId);
}
