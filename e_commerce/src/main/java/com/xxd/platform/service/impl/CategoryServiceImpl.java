package com.xxd.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xxd.platform.common.CustomExceptionHandle;
import com.xxd.platform.entity.Category;
import com.xxd.platform.entity.Dish;
import com.xxd.platform.entity.Setmeal;
import com.xxd.platform.mapper.CategoryMapper;
import com.xxd.platform.service.CategoryService;
import com.xxd.platform.service.DishService;
import com.xxd.platform.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService{
    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;
    @Override
    public void remove(Long id) {
        // CHECK WHETHRR CONNECT DISH
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper= new LambdaQueryWrapper<Dish>();

        //add check condition
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,id);

        int count = dishService.count(dishLambdaQueryWrapper);
        if(count > 0){
            //error
            throw new CustomExceptionHandle("connect with dish");

        }
        //CHECK CONNECT SETMEAL
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<Setmeal>();

        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,id);

        int count2 = setmealService.count(setmealLambdaQueryWrapper);
        if(count2 > 0){
            throw new CustomExceptionHandle("connect with setmeal");
        }
        super.removeById(id);
    }
}
