package com.uwb.commercialrentalmanagementapp.Service;

import com.uwb.commercialrentalmanagementapp.Model.Property;
import com.uwb.commercialrentalmanagementapp.Repository.PropertyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PropertyService {
    private final PropertyRepository propertyRepository;

    @Autowired
    public PropertyService(PropertyRepository propertyRepository) {
        this.propertyRepository = propertyRepository;
    }

    // Metoda do pobierania nieruchomości dla właściciela
    public List<Property> getPropertiesForOwner(Long ownerId) {
        // Użyj repozytorium do pobrania nieruchomości na podstawie ID właściciela
        return propertyRepository.findPropertiesByOwnerId(ownerId);
    }
}
