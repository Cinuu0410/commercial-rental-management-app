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

    @Column(name = "preferred_kind_of_property")
    private Long preferredKindOfProperty;

    public Tenant() {}
}
