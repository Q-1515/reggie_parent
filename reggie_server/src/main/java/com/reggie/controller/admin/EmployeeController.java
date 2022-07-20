package com.reggie.controller.admin;


import com.reggie.annotation.IgnoreToken;
import com.reggie.constant.JwtClaimsConstant;
import com.reggie.dto.EmployeeDTO;
import com.reggie.dto.EmployeeLoginDTO;
import com.reggie.dto.EmployeePageQueryDTO;
import com.reggie.dto.PasswordEditDTO;
import com.reggie.entity.Employee;
import com.reggie.properties.JwtProperties;
import com.reggie.result.PageResult;
import com.reggie.result.R;
import com.reggie.service.impl.EmployeeServiceImpl;
import com.reggie.utils.JwtUtil;
import com.reggie.vo.EmployeeLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

/**
 * 员工操作控制器
 */
@RestController
@RequestMapping("/admin/employee")
@Slf4j
@Api(tags = "员工相关接口")
public class EmployeeController {
    @Autowired
    private EmployeeServiceImpl employeeService;


    @Autowired
    private JwtProperties jwtProperties;   //jwt令牌相关配置类

    /**
     * 测试方法，用于测试jwt校验
     *
     * @return 返回success
     */
    @ApiOperation("Jwt测试接口")
    @IgnoreToken //自定义放行拦截注解
    @GetMapping("/testJwt")
    public R<String> testJwt() {
        return R.success("jwt test");
    }


    /**
     * 员工登录
     *
     * @param employeeLoginDTO 用户账号密码
     * @return 登录员工信息
     */
    @PostMapping("/login")
    @ApiOperation("员工登录接口")
    public R<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("员工登录：用户名{}，密码{}", employeeLoginDTO.getUsername(), employeeLoginDTO.getPassword());
        //调用业务登录返回对象
        Employee employeeLogin = employeeService.login(employeeLoginDTO);

        //设置jwt中有效载荷部分的数据，通常是用户的身份标识
        HashMap<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employeeLogin.getId());


        //创建jwt令牌
        String token = JwtUtil.createJWT(jwtProperties.getAdminSecretKey(), jwtProperties.getAdminTtl(), claims);

        //封装响应对象
        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employeeLogin.getId())
                .name(employeeLogin.getName())
                .userName(employeeLogin.getUsername())
                .token(token)
                .build();

        return R.success(employeeLoginVO);
    }

    /**
     * 员工退出
     *
     * @return 返回success
     */
    @PostMapping("/logout")
    @ApiOperation("员工退出接口")
    public R<String> logout() {
        return R.success("退出登录");
    }


    /**
     * 员工注册
     *
     * @param employeeDTO 获取添加的员工表单
     * @return 返回success
     */
    @PostMapping
    @ApiOperation("员工注册")
    public R<String> add(@RequestBody EmployeeDTO employeeDTO) {
        log.info("新增员工:{}", employeeDTO);
        employeeService.save(employeeDTO);
        return R.success();
    }

    /**
     * 员工信息查询接口
     *
     * @param employeePageQueryDTO (name,page,pageSize)
     * @return (total - 总记录数, records - 当前页数据集合)
     */
    @GetMapping("/page")
    @ApiOperation("员工信息查询接口")
    public R<PageResult> page(EmployeePageQueryDTO employeePageQueryDTO) {
        log.info("查询员工:{}", employeePageQueryDTO);
        PageResult pageResult = employeeService.PageQuery(employeePageQueryDTO);
        return R.success(pageResult);
    }

    /**
     * 启用、禁用员工接口
     *
     * @param status 状态
     * @param id     员工id
     * @return 返回success
     */
    @PostMapping("/status/{status}")
    @ApiOperation("启用、禁用员工接口")
    public R<String> startOrStop(@PathVariable Integer status, Long id) {
        log.info("启用、禁用员工接口");
        employeeService.startOrStop(status, id);
        return R.success("状态跟新成功");
    }

    /**
     * 根据id查询员工
     *
     * @return 员工对象
     */
    @GetMapping("/{id}")
    @ApiOperation("根据id查询员工接口")
    public R<Employee> getById(@PathVariable Long id) {
        log.info("根据id查询员工接口");
        return R.success(employeeService.getById(id));
    }

    /**
     * 编辑员工信息
     *
     * @return success
     */
    @PutMapping
    @ApiOperation("编辑员工信息接口")
    public R<String> update(@RequestBody EmployeeDTO employeeDTO) {
        log.info("编辑员工信息接口");
        employeeService.update(employeeDTO);
        return R.success("修改成功");
    }

    /**
     * 员工修改密码
     *
     * @return success
     */
    @PutMapping("/editPassword")
    @ApiOperation("员工修改密码")
    public R<String> editPassword(@RequestBody PasswordEditDTO passwordEditDTO) {
        log.info("员工修改密码");
        employeeService.editPassword(passwordEditDTO);
        return R.success("修改成功");
    }

}
