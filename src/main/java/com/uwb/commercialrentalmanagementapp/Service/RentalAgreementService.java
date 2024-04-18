package com.uwb.commercialrentalmanagementapp.Service;

import com.uwb.commercialrentalmanagementapp.Model.RentalAgreement;
import com.uwb.commercialrentalmanagementapp.Repository.RentalAgreementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RentalAgreementService {
    private final RentalAgreementRepository rentalAgreementRepository;

    @Autowired
    public RentalAgreementService(RentalAgreementRepository rentalAgreementRepository) {
        this.rentalAgreementRepository = rentalAgreementRepository;
    }

    public List<RentalAgreement> getRentalAgreementsForUser(Long userId) {
        return rentalAgreementRepository.findRentalAgreementsByTenantUserId(userId);
    }

    // Metoda do pobierania rentalAgreementId na podstawie propertyId
    public Long getRentalAgreementIdByPropertyId(Long propertyId) {
        RentalAgreement rentalAgreement = rentalAgreementRepository.findByProperty_PropertyId(propertyId)
                .orElseThrow(() -> new IllegalArgumentException("Rental agreement not found for propertyId: " + propertyId));
        return rentalAgreement.getAgreementId();
    }
}
