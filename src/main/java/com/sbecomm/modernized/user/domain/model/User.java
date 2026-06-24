package com.sbecomm.modernized.user.domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class User {
    @NonNull private final UserId id; // Keycloak UUID maps directly to this
    private String email;
    private String firstName;
    private String lastName;
    private final List<Address> addresses = new ArrayList<>();

    public User(UserId id, String email, String firstName, String lastName) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public void updateProfile(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public void addAddress(Address address) {
        if (address.isDefault()) {
            this.addresses.forEach(Address::clearDefault);
        } else if (this.addresses.isEmpty()) {
            address.setDefault();
        }
        this.addresses.add(address);
    }

    public void removeAddress(Address address) {
        this.addresses.remove(address);
        if (address.isDefault() && !this.addresses.isEmpty()) {
            this.addresses.getFirst().setDefault();
        }
    }

    public List<Address> getAddresses() { 
        return Collections.unmodifiableList(addresses); 
    }
}
