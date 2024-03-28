package com.uwb.commercialrentalmanagementapp.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import jakarta.persistence.*;

@Data
@Entity
@Table(name = "properties")
public class Property {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "property_id")
    private Long propertyId;

    @Column(name = "address")
    private String address;

    @Column(name = "area")
    private String area;

    @Column(name = "number_of_rooms")
    private int numberOfRooms;

    public Property() {
        // Domyślny konstruktor
    }

    public Property(String address, String area, int numberOfRooms) {
        this.address = address;
        this.area = area;
        this.numberOfRooms = numberOfRooms;
    }

    // Getters and setters
}