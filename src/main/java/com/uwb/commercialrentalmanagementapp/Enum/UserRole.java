package com.uwb.commercialrentalmanagementapp.Enum;

public enum UserRole {
    USER("USER"),
    SUPER_USER("SUPER_USER"),
    ;
    private final String roleName;
    UserRole(String roleName) {
        this.roleName = roleName;
    }
    public String getRoleName() {
        return roleName;
    }
}
