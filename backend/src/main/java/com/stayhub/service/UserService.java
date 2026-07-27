package com.stayhub.service;

import com.stayhub.dto.UserResponse;
import com.stayhub.entity.User;

public interface UserService {

    UserResponse becomeHost(User currentUser);
}
