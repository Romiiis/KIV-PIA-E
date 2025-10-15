package com.romiiis.controller;

import com.romiiis.mapper.UserMapper;
import com.romiiis.service.interfaces.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class UserController implements UsersApi {

    private final IUserService IUserService;
    private final UserMapper userMapper;


}
