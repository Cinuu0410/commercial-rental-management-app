package com.uwb.commercialrentalmanagementapp.Service;

import com.uwb.commercialrentalmanagementapp.Model.Property;
import com.uwb.commercialrentalmanagementapp.Model.TypeOfProperty;
import com.uwb.commercialrentalmanagementapp.Repository.PropertyRepository;
import com.uwb.commercialrentalmanagementapp.Repository.RentPaymentRepository;
import com.uwb.commercialrentalmanagementapp.Repository.RentalAgreementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PropertyService {
    private final PropertyRepository propertyRepository;
    private final RentalAgreementRepository rentalAgreementRepository;
    private final RentPaymentRepository rentPaymentRepository;

    @Autowired
    public PropertyService(PropertyRepository propertyRepository, RentalAgreementRepository rentalAgreementRepository, RentalAgreementRepository rentalAgreementRepository1, RentPaymentRepository rentPaymentRepository) {
        this.propertyRepository = propertyRepository;
        this.rentalAgreementRepository = rentalAgreementRepository1;
        this.rentPaymentRepository = rentPaymentRepository;
    }

    // Metoda do pobierania nieruchomości dla właściciela
    public List<Property> getPropertiesForOwner(Long ownerId) {
        // Użyj repozytorium do pobrania nieruchomości na podstawie ID właściciela
        return propertyRepository.findPropertiesByOwnerId(ownerId);
    }

    public List<TypeOfProperty> getAllTypesOfProperties() {
        return propertyRepository.findAllTypesOfProperties();
    }


//    public BigDecimal getAnnualPaymentAmount(Long rentalAgreementId) {
//        // Pobierz wszystkie płatności czynszu dla danej umowy najmu
//        List<RentPayment> rentPayments = rentPaymentRepository.findAllByRentalAgreementId(rentalAgreementId);
//
//        BigDecimal totalPaymentAmount = BigDecimal.ZERO;
//        for (RentPayment payment : rentPayments) {
//            // Dodaj kwotę płatności do całkowitej sumy
//            totalPaymentAmount = totalPaymentAmount.add(payment.getPaymentAmount());
//        }
//
//        // Zakładając, że płatności czynszu są miesięczne, pomnóż sumę przez 12, aby uzyskać roczny przychód
//        totalPaymentAmount = totalPaymentAmount.multiply(BigDecimal.valueOf(12));
//
//        // Dodaj logi
//        System.out.println("Rental Agreement ID: " + rentalAgreementId);
//        System.out.println("Total payment amount: " + totalPaymentAmount);
//
//        return totalPaymentAmount;
//    }
}
