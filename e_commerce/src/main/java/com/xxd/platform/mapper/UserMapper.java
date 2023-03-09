package com.xxd.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xxd.platform.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User>{
}
