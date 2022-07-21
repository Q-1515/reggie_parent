package com.reggie.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.reggie.constant.MessageConstant;
import com.reggie.constant.PasswordConstant;
import com.reggie.constant.StatusConstant;
import com.reggie.context.BaseContext;
import com.reggie.dto.EmployeeDTO;
import com.reggie.dto.EmployeeLoginDTO;
import com.reggie.dto.EmployeePageQueryDTO;
import com.reggie.dto.PasswordEditDTO;
import com.reggie.entity.Employee;
import com.reggie.exception.AccountLockedException;
import com.reggie.exception.AccountNotFoundException;
import com.reggie.exception.PasswordEditFailedException;
import com.reggie.exception.PasswordErrorException;
import com.reggie.mapper.EmployeeMapper;
import com.reggie.result.PageResult;
import com.reggie.service.EmployeeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;

@Service
public class EmployeeServiceImpl implements EmployeeService {
    @Autowired
    private EmployeeMapper employeeMapper;

    //员工登录
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        Employee employee = employeeMapper.getByUsername(username);

        //用户不存在
        if (employee == null) {
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //账号锁定
        if (employee.getStatus() == StatusConstant.DISABLE) {
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //密码错误
        //对前端传递过来的明文密码进行md5加密处理
        String pwd = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!pwd.equals(employee.getPassword())) {
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        return employee;
    }

    //员工添加
    public void save(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();
        //对象的拷贝
        BeanUtils.copyProperties(employeeDTO, employee);

        //设置默认密码
        employee.setPassword(DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes()));

        //设置账号状态
        employee.setStatus(StatusConstant.ENABLE);


//        //获取ThreadLocal中的empId(员工id)
//        Long empId = BaseContext.getCurrentId();
//        //创建的用户
//        employee.setCreateUser(empId);
//        //修改的用户
//        employee.setUpdateUser(empId);
//
//        //设置员工创建时间
//        employee.setCreateTime(LocalDateTime.now());
//        //设置员工修改时间
//        employee.setUpdateTime(LocalDateTime.now());

        employeeMapper.save(employee);

    }


    //员工分页查询
    public PageResult PageQuery(EmployeePageQueryDTO employeePageQueryDTO) {
        //分页查询插件自动生成分页功能
        PageHelper.startPage(employeePageQueryDTO.getPage(), employeePageQueryDTO.getPageSize());
        Page<Employee> page = employeeMapper.PageQuery(employeePageQueryDTO);

        //返回封装PageResult对象
        return new PageResult(page.getTotal(), page.getResult());
    }


    //账号的启用和禁用
    public void startOrStop(Integer status, Long id) {
        Employee employee = new Employee();
        //设置员工状态
        employee.setStatus(status);
        //设置员工id
        employee.setId(id);

//        //设置员工修改时间
//        employee.setUpdateTime(LocalDateTime.now());
//
//        //设置修改人
//        employee.setUpdateUser(BaseContext.getCurrentId());

        employeeMapper.updateStartusById(employee);
    }

    //根据id查询员工
    public Employee getById(Long id) {
        return employeeMapper.getById(id);
    }

    //编辑员工信息
    public void update(EmployeeDTO employeeDTO) {
        //将参数复制到员工对象
        Employee employee = new Employee();
        BeanUtils.copyProperties(employeeDTO, employee);

//        //设置员工修改时间
//        employee.setUpdateTime(LocalDateTime.now());
//
//        //设置修改人
//        employee.setUpdateUser(BaseContext.getCurrentId());

        employeeMapper.update(employee);

    }

    //员工修改密码
    @Override
    public void editPassword(PasswordEditDTO passwordEditDTO) {
        String oldPassword = passwordEditDTO.getOldPassword();
        String newPassword = passwordEditDTO.getNewPassword();

        //根据token令牌获取id
        Long empId = BaseContext.getCurrentId();

        //根据返还对象
        Employee employee = employeeMapper.getById(empId);

        //判断对象是否为空
        if (employee == null) {
            throw new PasswordEditFailedException(MessageConstant.PASSWORD_EDIT_FAILED);
        }

        //对旧密码明文进行md5
        oldPassword = DigestUtils.md5DigestAsHex(oldPassword.getBytes());

        //判断旧密码书否正确
        if (!employee.getPassword().equals(oldPassword)) {
            throw new PasswordErrorException(MessageConstant.PASSWORD_EDIT_FAILED);
        }

        //对新密码明文进行md5
        newPassword = DigestUtils.md5DigestAsHex(newPassword.getBytes());
        Employee emp = Employee.builder()
                .id(empId)
                .password(newPassword).build();

        employeeMapper.update(emp);
    }
}
