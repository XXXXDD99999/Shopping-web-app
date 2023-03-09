package com.xxd.platform.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xxd.platform.dto.DishDto;
import com.xxd.platform.entity.DishFlavor;
import com.xxd.platform.mapper.DishFlavorMapper;
import com.xxd.platform.service.DishFlavorService;
import org.springframework.stereotype.Service;

@Service
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper, DishFlavor> implements DishFlavorService {

}
