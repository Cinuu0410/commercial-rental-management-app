package com.uwb.commercialrentalmanagementapp.Service;

import com.uwb.commercialrentalmanagementapp.Model.RentPayment;
import com.uwb.commercialrentalmanagementapp.Model.RentalAgreement;
import com.uwb.commercialrentalmanagementapp.Repository.RentPaymentRepository;
import com.uwb.commercialrentalmanagementapp.Repository.RentalAgreementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class RentPaymentService {

    private final RentPaymentRepository rentPaymentRepository;
    private final RentalAgreementRepository rentalAgreementRepository;

    @Autowired
    public RentPaymentService(RentPaymentRepository rentPaymentRepository, RentalAgreementRepository rentalAgreementRepository) {
        this.rentPaymentRepository = rentPaymentRepository;
        this.rentalAgreementRepository = rentalAgreementRepository;
    }

    public String getStatus(Long propertyId) {
        try {
            // Znajdź umowę najmu dla podanej nieruchomości
            Optional<RentalAgreement> rentalAgreementOptional = rentalAgreementRepository.findByProperty_PropertyId(propertyId);
            if (rentalAgreementOptional.isPresent()) {
                RentalAgreement rentalAgreement = rentalAgreementOptional.get();

                // Znajdź płatność za umowę najmu
                Optional<RentPayment> rentPaymentOptional = rentPaymentRepository.findByRentalAgreementId(rentalAgreement.getAgreementId());
                if (rentPaymentOptional.isPresent()) {
                    RentPayment rentPayment = rentPaymentOptional.get();

                    // Sprawdź status płatności i zwróć odpowiedni komunikat
                    return rentPayment.getStatus().equalsIgnoreCase("paid") ? "opłacone" : "nieopłacone";
                } else {
                    throw new IllegalArgumentException("Rent payment not found for propertyId: " + propertyId);
                }
            } else {
                throw new IllegalArgumentException("Rental agreement not found for propertyId: " + propertyId);
            }
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("Rent payment not found")) {
                return "brak rent payment dla danej nieruchomości";
            } else if (e.getMessage().contains("Rental agreement not found")) {
                return "brak umowy najmu dla danej nieruchomości";
            }
            // jeśli wystąpi inny błąd, możesz go tutaj obsłużyć
            return "Wystąpił błąd: " + e.getMessage();
        }
    }

    public RentPayment createInvoice(Long propertyId) {
        RentalAgreement rentalAgreement = rentalAgreementRepository.findByProperty_PropertyId(propertyId)
                .orElseThrow(() -> new IllegalArgumentException("Rental agreement not found for propertyId: " + propertyId));

        RentPayment rentPayment = new RentPayment();
        rentPayment.setPaymentDate(new Date());
        rentPayment.setRentalAgreementId(rentalAgreement.getAgreementId());
        rentPayment.setStatus("unpaid");

        return rentPaymentRepository.save(rentPayment);
    }
}