package com.reggie.service;

import com.reggie.dto.EmployeeDTO;
import com.reggie.dto.EmployeeLoginDTO;
import com.reggie.dto.EmployeePageQueryDTO;
import com.reggie.entity.Employee;
import com.reggie.result.PageResult;

public interface EmployeeService {
    //员工登录
    Employee login(EmployeeLoginDTO employeeLoginDTO);

    //添加员工
    void save(EmployeeDTO employeeDTO);

    //分页查询员工
   PageResult PageQuery(EmployeePageQueryDTO employeePageQueryDTO);
}
