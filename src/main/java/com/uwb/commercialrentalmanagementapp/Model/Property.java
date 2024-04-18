package com.uwb.commercialrentalmanagementapp.Model;

import jakarta.persistence.*;
import lombok.Data;

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

    @Column(name="owner_id")
    private Long ownerId;

    public Property() {}

    public Property(String address, String area, int numberOfRooms, Long ownerId) {
        this.address = address;
        this.area = area;
        this.numberOfRooms = numberOfRooms;
        this.ownerId = ownerId;
    }
}