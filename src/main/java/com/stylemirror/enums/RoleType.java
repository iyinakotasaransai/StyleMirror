package com.stylemirror.enums;

public enum RoleType {
    ROLE_BUYER,
    ROLE_SELLER,
    ROLE_ADMIN;

    public static RoleType fromString(String role) {
        if (role == null) return ROLE_BUYER;
        String clean = role.trim().toUpperCase();
        if (!clean.startsWith("ROLE_")) {
            clean = "ROLE_" + clean;
        }
        for (RoleType rt : values()) {
            if (rt.name().equalsIgnoreCase(clean)) {
                return rt;
            }
        }
        return ROLE_BUYER;
    }
}
