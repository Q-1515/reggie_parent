package com.reggie.mapper;

import com.github.pagehelper.Page;
import com.reggie.dto.EmployeePageQueryDTO;
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


    /**
     * 添加员工
     * @param employee 员工对象
     */
    void save(Employee employee);


    /**
     * 查询员工
     * @param employeePageQueryDTO (name,page,pageSize)
     * @return  PageResult(分页个数,员工集合)
     */
    Page<Employee> PageQuery(EmployeePageQueryDTO employeePageQueryDTO);
}
