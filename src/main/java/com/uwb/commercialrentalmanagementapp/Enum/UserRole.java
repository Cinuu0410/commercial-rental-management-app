package com.uwb.commercialrentalmanagementapp.Enum;

public enum UserRole {
    ADMINISTRATOR("Administrator"),
    WYNAJMUJACY("Wynajmujacy"),
    NAJEMCA("Najemca");

    private final String roleName;
    UserRole(String roleName) {
        this.roleName = roleName;
    }
    public String getRoleName() {
        return roleName;
    }
}
