package com.reggie.service;

import com.reggie.dto.UserLoginDTO;
import com.reggie.entity.User;

public interface UserService {

    //微信登录
    User wxLogin(UserLoginDTO userLoginDTO);
}
