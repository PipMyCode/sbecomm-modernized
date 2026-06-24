package com.sbecomm.modernized.user.domain.repository;

import com.sbecomm.modernized.user.domain.model.User;
import com.sbecomm.modernized.user.domain.model.UserId;
import java.util.Optional;

public interface UserRepository {
    Optional<User> findById(UserId id);
    User save(User user);
    void deleteById(UserId id);
}
