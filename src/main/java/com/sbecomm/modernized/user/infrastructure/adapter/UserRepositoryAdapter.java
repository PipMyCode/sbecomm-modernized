package com.sbecomm.modernized.user.infrastructure.adapter;

import com.sbecomm.modernized.user.domain.model.Address;
import com.sbecomm.modernized.user.domain.model.User;
import com.sbecomm.modernized.user.domain.model.UserId;
import com.sbecomm.modernized.user.domain.repository.UserRepository;
import com.sbecomm.modernized.user.infrastructure.entity.AddressEntity;
import com.sbecomm.modernized.user.infrastructure.entity.UserEntity;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class UserRepositoryAdapter implements UserRepository {

    private final UserJpaRepository jpaRepository;

    public UserRepositoryAdapter(UserJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<User> findById(UserId id) {
        return jpaRepository.findById(id.value()).map(this::toDomain);
    }

    @Override
    public User save(User user) {
        UserEntity entity = toEntity(user);
        UserEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public void deleteById(UserId id) {
        jpaRepository.deleteById(id.value());
    }

    private User toDomain(UserEntity entity) {
        User user = new User(
                new com.sbecomm.modernized.user.domain.model.UserId(entity.getId()),
                entity.getEmail(),
                entity.getFirstName(),
                entity.getLastName()
        );
        if (entity.getAddresses() != null) {
            for (AddressEntity addrEntity : entity.getAddresses()) {
                Address address = new Address(
                    addrEntity.getStreet(),
                    addrEntity.getCity(),
                    addrEntity.getState(),
                    addrEntity.getZipCode(),
                    addrEntity.getCountry(),
                    addrEntity.isDefault()
                );
                user.addAddress(address);
            }
        }
        return user;
    }

    private UserEntity toEntity(User user) {
        UserEntity entity = new UserEntity();
        entity.setId(user.getId().value());
        entity.setEmail(user.getEmail());
        entity.setFirstName(user.getFirstName());
        entity.setLastName(user.getLastName());
        
        if (user.getAddresses() != null) {
            entity.setAddresses(user.getAddresses().stream().map(addr -> {
                AddressEntity aEntity = new AddressEntity();
                aEntity.setUser(entity);
                aEntity.setStreet(addr.getStreet());
                aEntity.setCity(addr.getCity());
                aEntity.setState(addr.getState());
                aEntity.setZipCode(addr.getZipCode());
                aEntity.setCountry(addr.getCountry());
                aEntity.setDefault(addr.isDefault());
                return aEntity;
            }).collect(Collectors.toList()));
        }
        return entity;
    }
}
