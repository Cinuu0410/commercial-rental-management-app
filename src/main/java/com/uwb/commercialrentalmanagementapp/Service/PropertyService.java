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

    public List<Property> getPropertiesForOwner(Long ownerId) {
        return propertyRepository.findPropertiesByOwnerId(ownerId);
    }

    public List<TypeOfProperty> getAllTypesOfProperties() {
        return propertyRepository.findAllTypesOfProperties();
    }
}
