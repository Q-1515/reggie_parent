package com.reggie.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.reggie.constant.MessageConstant;
import com.reggie.dto.UserLoginDTO;
import com.reggie.entity.User;
import com.reggie.exception.LoginFailedException;
import com.reggie.mapper.UserMapper;
import com.reggie.service.UserService;
import com.reggie.utils.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    public static final String LOGIN_URL = "https://api.weixin.qq.com/sns/jscode2session";

    @Autowired
    private UserMapper userMapper;

    @Value("${reggie.wechat.appid}")
    private String appid;

    @Value("${reggie.wechat.secret}")
    private String secret;


    private String getOpenid(String code) {
        Map<String, String> param = new HashMap<>();
        param.put("appid",appid);
        param.put("secret",secret);
        param.put("js_code",code);
        param.put("grant_type","authorization_code");

        //数据格式:{openid:xxxx,session_key:xxxx}
        String res = HttpClientUtil.doGet(LOGIN_URL, param);
        JSONObject jsonObject = JSON.parseObject(res);
        String openid = jsonObject.getString("openid");
        log.info("查询到微信用户的openid:{}", openid);
        return openid;
    }

    /**
     * 微信登陆
     *
     * @param userLoginDTO 微信授权码
     * @return 用户信息
     */
    public User wxLogin(UserLoginDTO userLoginDTO) {
        String code = userLoginDTO.getCode();

        //获取微信用户的openid
        String openid = getOpenid(code);

        if (openid == null) {
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }

        //根据openid查询用户
        User user = userMapper.getByOpenid(openid);
        if (user == null) {
            //新用户自动注册
            user = new User();
            user.setOpenid(openid);
            user.setCreateTime(LocalDate.now());
            userMapper.insert(user);
        }
        return user;
    }

}
