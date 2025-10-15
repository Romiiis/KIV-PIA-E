package com.romiiis.service.impl;


import com.romiiis.domain.User;
import com.romiiis.repository.IUserRepository;
import com.romiiis.service.interfaces.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class DefaultUserServiceImpl implements IUserService {

    private final IUserRepository userRepository;

    @Override
    public User getUserByEmail(String email) {
        return userRepository.getUserByEmail(email);
    }

    @Override
    public User createNewCustomer(String name, String email, String password) {

        User newUser = User.createCustomer(name,email).withHashedPassword(password);
        userRepository.saveUser(newUser);
        log.info("New customer created: {}", newUser.getEmailAddress());
        return newUser;
    }

    @Override
    public User createNewTranslator(String name, String email, Set<Locale> langs, String password) {
        User newUser = User.createTranslator(name,email,langs).withHashedPassword(password);
        userRepository.saveUser(newUser);
        log.info("New translator created: {}", newUser.getEmailAddress());
        return newUser;
    }


}
