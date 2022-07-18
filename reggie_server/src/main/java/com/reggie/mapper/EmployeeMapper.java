package com.reggie.mapper;

import com.reggie.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmployeeMapper {
    /**
     * 根据用户名查询员工
     * @param username  用户名
     * @return  员工对象
     */
    Employee getByUsername(String username);

}
