package com.xxd.platform.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xxd.platform.common.R;
import com.xxd.platform.entity.Employee;
import com.xxd.platform.service.EmployeeService;

import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){
        // 根据提交来的密码，进行MD5加密
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //2根据页面提交的用户名查数据库
        LambdaQueryWrapper<Employee> queryWrapper =  new LambdaQueryWrapper<Employee>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);

        //3.没查到用户就返回error
        if(emp == null){
            return R.error("登入失败");
        }

        //4. 密码不对返回error
        if (!emp.getPassword().equals(password)) {
            return R.error("密码错误");
        }

        //5. 查看是否可用
        if(emp.getStatus() == 0){
            return R.error("账号不可用");
        }

        // 6.登入成功， 将员工id存入session,并返回登入结果
        request.getSession().setAttribute("employee", emp.getId());
        return R.success(emp);
    }

    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        request.getSession().removeAttribute("employee");
        return R.success("logout success");
    }


    @PostMapping
    public R<String> save(HttpServletRequest request,@RequestBody Employee employee){
        log.info("user info: {}", employee.toString());

        //initial password and use md5
        String password = "123456";
        employee.setPassword(DigestUtils.md5DigestAsHex(password.getBytes()));
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());
//
        Long empId = (Long) request.getSession().getAttribute("employee");
//        employee.setCreateUser(empId);
//        employee.setUpdateUser(empId);

        employeeService.save(employee);
        return R.success("created new employee");
    }


    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){
        log.info(" page = {}, pagesize = {}, name = {}", page,pageSize,name);

        //construct page
        Page pageInfo = new Page(page, pageSize);

        // tiao jian guo lv qi
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper();

        if(StringUtils.isNotEmpty(name)) {
            queryWrapper.like(Employee::getName,name);
        }


        //order by
        queryWrapper.orderByDesc(Employee::getUpdateTime);

        employeeService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }

    @PutMapping
    public R<String> update(HttpServletRequest request,@RequestBody Employee employee){
//        Long empId = (Long)request.getSession().getAttribute("employee");
//        employee.setUpdateTime(LocalDateTime.now());
//        employee.setUpdateUser(empId);
        employeeService.updateById(employee);

        return R.success("change success");
    }

    @GetMapping("{id}")
    public R<Employee> getById(@PathVariable Long id){
        log.info("search employee by id");
        Employee emp = employeeService.getById(id);
        if(emp != null) {
            return R.success(emp);
        }
        return R.error("no such employee");
    }
}
