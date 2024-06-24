package com.uwb.commercialrentalmanagementapp.Repository;

import com.uwb.commercialrentalmanagementapp.Model.Property;
import com.uwb.commercialrentalmanagementapp.Model.TypeOfProperty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PropertyRepository extends JpaRepository<Property, Long> {
    List<Property> findPropertiesByOwnerId(Long ownerId);

    @Query("SELECT t FROM TypeOfProperty t")
    List<TypeOfProperty> findAllTypesOfProperties();
}
