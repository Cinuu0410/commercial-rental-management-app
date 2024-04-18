package com.uwb.commercialrentalmanagementapp.Repository;

import com.uwb.commercialrentalmanagementapp.Model.RentPayment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RentPaymentRepository extends JpaRepository<RentPayment, Long> {


    Optional<RentPayment> findByRentalAgreementId(Long rentalAgreementId);
}
