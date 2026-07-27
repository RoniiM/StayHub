package com.stayhub.service.impl;

import com.stayhub.dto.UserResponse;
import com.stayhub.entity.User;
import com.stayhub.entity.enums.UserRole;
import com.stayhub.exception.ResourceNotFoundException;
import com.stayhub.mapper.UserMapper;
import com.stayhub.repository.UserRepository;
import com.stayhub.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public UserResponse becomeHost(User currentUser) {
        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + currentUser.getId()));

        user.getRoles().add(UserRole.ROLE_HOST);
        log.info("User promoted to host: userId={}", user.getId());

        return userMapper.toResponse(user);
    }
}
