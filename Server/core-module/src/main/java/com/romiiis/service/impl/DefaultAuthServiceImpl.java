package com.romiiis.service.impl;

import com.romiiis.service.interfaces.IAuthService;
import com.romiiis.service.interfaces.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class DefaultAuthServiceImpl implements IAuthService {

    private final IUserService userService;


    @Override
    public String registerCustomer(String name, String email, String hashPassword) {
        userService.createNewCustomer(name, email, hashPassword);
        return "JWT_TOKEN";

    }

    @Override
    public String registerTranslator(String name, String email, Set<Locale> langs, String hashPassword) {
        userService.createNewTranslator(name, email, langs, hashPassword);
        return "JWT_TOKEN";
    }
}
