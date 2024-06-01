package com.uwb.commercialrentalmanagementapp.Model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "types_of_properties")
public class TypeOfProperty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "type_id")
    private Long typeId;

    @Column(name = "kind")
    private String kind;


    public TypeOfProperty() {}

}
