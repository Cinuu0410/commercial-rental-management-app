package com.uwb.commercialrentalmanagementapp.Model;

import jakarta.persistence.*;
import lombok.Data;
@Data
@Entity
@Table(name = "tenants")
public class Tenant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tenant_id")
    private Long tenantId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "address")
    private String address;

    @Column(name = "city")
    private String city;

    @Column(name = "post_code")
    private String postCode;

    public Tenant() {
        // Domyślny konstruktor
    }

    public Tenant(Long userId, String phoneNumber, String address, String city, String postCode) {
        this.userId = userId;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.city = city;
        this.postCode = postCode;
    }

    // Getters and setters
}
