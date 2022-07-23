package com.reggie.controller.user;

import com.reggie.constant.JwtClaimsConstant;
import com.reggie.dto.UserLoginDTO;
import com.reggie.entity.User;
import com.reggie.properties.JwtProperties;
import com.reggie.result.R;
import com.reggie.service.UserService;
import com.reggie.utils.JwtUtil;
import com.reggie.vo.UserLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user/user")
@Slf4j
@Api(tags = "C端-用户接口")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * C端用户登录--微信登录
     *
     * @param userLoginDTO //微信授权码
     * @return 用户id，微信openid，jwt令牌
     */
    @PostMapping("/login")
    @ApiOperation("登录")
    public R<UserLoginVO> login(@RequestBody UserLoginDTO userLoginDTO) {
        log.info("微信小程序登录:{}", userLoginDTO);
        User user = userService.wxLogin(userLoginDTO);

        //设置用户id为唯一参数
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.USER_ID, user.getId());
        //创建jwt令牌
        String token = JwtUtil.createJWT(jwtProperties.getUserSecretKey(), jwtProperties.getUserTtl(), claims);
        UserLoginVO loginVO = UserLoginVO.builder()
                .id(user.getId())
                .openid(user.getOpenid())
                .token(token)
                .build();
        return R.success(loginVO);
    }

    /**
     * 退出
     *
     * @return success
     */
    @PostMapping("/logout")
    @ApiOperation("退出")
    public R<String> logout() {
        return R.success();
    }
}
