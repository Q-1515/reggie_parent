package com.reggie.mapper;

import com.github.pagehelper.Page;
import com.reggie.annotation.AutoFill;
import com.reggie.constant.AutoFillConstant;
import com.reggie.dto.EmployeePageQueryDTO;
import com.reggie.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmployeeMapper {
    /**
     * 根据用户名查询员工
     *
     * @param username 用户名
     * @return 员工对象
     */
    Employee getByUsername(String username);


    /**
     * 添加员工
     *
     * @param employee 员工对象
     */
    @AutoFill(type = AutoFillConstant.INSERT)
    void save(Employee employee);


    /**
     * 查询员工
     *
     * @param employeePageQueryDTO (name,page,pageSize)
     * @return PageResult(分页个数, 员工集合)
     */
    Page<Employee> PageQuery(EmployeePageQueryDTO employeePageQueryDTO);

    /**
     * 修改员工状态
     */
    @AutoFill(type = AutoFillConstant.UPDATE)
    void updateStartusById(Employee employee);


    /**
     * 根据id回显员工
     * @param id 员工id
     * @return 员工对象
     */
    Employee getById(Long id);


    /**
     * 编辑员工信息
     * @param employee 员工参数
     */
    @AutoFill(type = AutoFillConstant.UPDATE)
    void update(Employee employee);
}
