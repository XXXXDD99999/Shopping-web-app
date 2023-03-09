package com.xxd.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xxd.platform.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {

}
