package com.sbecomm.modernized.user.domain.model;

import lombok.Getter;
import lombok.EqualsAndHashCode;
import lombok.AllArgsConstructor;

@Getter
@EqualsAndHashCode(exclude = {"isDefault"})
@AllArgsConstructor
public class Address {
    private final String street;
    private final String city;
    private final String state;
    private final String zipCode;
    private final String country;
    private boolean isDefault;

    public void setDefault() {
        this.isDefault = true;
    }

    public void clearDefault() {
        this.isDefault = false;
    }
}
