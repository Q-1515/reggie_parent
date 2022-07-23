package com.reggie.service;

import com.reggie.dto.EmployeeDTO;
import com.reggie.dto.EmployeeLoginDTO;
import com.reggie.dto.EmployeePageQueryDTO;
import com.reggie.dto.PasswordEditDTO;
import com.reggie.entity.Employee;
import com.reggie.result.PageResult;

/**
 * 员工操作
 */
public interface EmployeeService {
    //员工登录
    Employee login(EmployeeLoginDTO employeeLoginDTO);

    //添加员工
    void save(EmployeeDTO employeeDTO);

    //分页查询员工
   PageResult PageQuery(EmployeePageQueryDTO employeePageQueryDTO);

    //账号的启用和禁用
    void startOrStop(Integer status, Long id);

    //根据id查询员工
    Employee getById(Long id);

    //编辑员工信息
    void update(EmployeeDTO employeeDTO);

    //员工修改密码
    void editPassword(PasswordEditDTO passwordEditDTO);
}
