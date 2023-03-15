package com.xxd.platform.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xxd.platform.entity.ShoppingCart;
import com.xxd.platform.mapper.ShoppingCartMapper;
import com.xxd.platform.service.ShoppingCartService;
import org.springframework.stereotype.Service;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {

}
