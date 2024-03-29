package com.uwb.commercialrentalmanagementapp.Model;

import com.uwb.commercialrentalmanagementapp.Enum.UserRole;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "app_user")
public class User {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long Id;
        private String username;
        private String password;
        private String firstName;
        private String lastName;
        private String email;
        private String role;


        // Dodano pole wallet do encji User
        @Column(columnDefinition = "DECIMAL(10,2) DEFAULT 0.0")
        private BigDecimal wallet;

        public User() {
                // Domyślny konstruktor
        }

        public User(String username, String password, String firstName, String lastName, String email, UserRole role) {
                this.username = username;
                this.password = password;
                this.firstName = firstName;
                this.lastName = lastName;
                this.email = email;
                this.role = role.getRoleName(); // Ustawienie roli użytkownika
        }


        @Transient
        private String rawPassword;
}
