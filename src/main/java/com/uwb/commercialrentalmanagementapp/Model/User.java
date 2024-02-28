package com.uwb.commercialrentalmanagementapp.Model;

import com.uwb.commercialrentalmanagementapp.Enum.UserRole;
import jakarta.persistence.*;
import lombok.Data;
@Data
@Entity
@Table(name = "user")
public class User {

        @jakarta.persistence.Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long Id;
        private String username;
        private String password;
        private String firstName;
        private String lastName;
        private String email;
        private String role;


        public User() {
            this.role = String.valueOf(UserRole.USER);
        }

        @Transient
        private String rawPassword;
}
