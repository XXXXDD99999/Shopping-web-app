package com.xxd.platform.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xxd.platform.entity.Employee;
import com.xxd.platform.mapper.EmployeeMapper;
import com.xxd.platform.service.EmployeeService;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {

}
