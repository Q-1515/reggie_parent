package com.reggie.service;

import com.reggie.dto.EmployeeLoginDTO;
import com.reggie.entity.Employee;

public interface EmployeeService {
    Employee login(EmployeeLoginDTO employeeLoginDTO);
}
