package com.uwb.commercialrentalmanagementapp.Repository;

import com.uwb.commercialrentalmanagementapp.Model.Property;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PropertyRepository extends JpaRepository<Property, Long> {
    // Metoda do pobierania nieruchomości na podstawie ID właściciela
    List<Property> findPropertiesByOwnerId(Long ownerId);
}
