package com.uwb.commercialrentalmanagementapp.Repository;

import com.uwb.commercialrentalmanagementapp.Model.RentalAgreement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RentalAgreementRepository extends JpaRepository<RentalAgreement, Long> {
    List<RentalAgreement> findRentalAgreementsByTenantUserId(Long userId);
}
