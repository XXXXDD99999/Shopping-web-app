package com.xxd.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xxd.platform.dto.DishDto;
import com.xxd.platform.dto.SetmealDto;
import com.xxd.platform.entity.Dish;
import com.xxd.platform.entity.Setmeal;
import com.xxd.platform.entity.SetmealDish;
import com.xxd.platform.mapper.SetmealMapper;
import com.xxd.platform.service.SetmealDishService;
import com.xxd.platform.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {
    @Autowired
    private SetmealDishService setmealDishService;


    @Override
    public void saveWithDishes(SetmealDto setmealDto) {
        //save setmeal
        this.save(setmealDto);

        Long id = setmealDto.getId();
        List<SetmealDish> setmealDishList = setmealDto.getSetmealDishes();

        setmealDishList = setmealDishList.stream().map((item) ->{
            item.setSetmealId(id);
            return item;
        }).collect(Collectors.toList());

        setmealDishService.saveBatch(setmealDishList);
    }

    @Override
    public SetmealDto getByIdWithDish(Long id) {
        Setmeal setmeal = this.getById(id);
        SetmealDto setmealDto = new SetmealDto();
        BeanUtils.copyProperties(setmeal, setmealDto);

        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(id != null, SetmealDish::getId, setmeal.getId());


        List<SetmealDish> setmealDishList = setmealDishService.list(queryWrapper);

        setmealDto.setSetmealDishes(setmealDishList);
        return setmealDto;
    }

    @Override
    public void updateWithDish(SetmealDto setmealDto) {
        //save new setmeal
        this.updateById(setmealDto);

        Long setmealId = setmealDto.getId();
        // clear setmealdish table

        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(setmealId !=null, SetmealDish::getSetmealId, setmealId);

        setmealDishService.remove(queryWrapper);

        // add new dish tp setmeal dish table
        List<SetmealDish> list = setmealDto.getSetmealDishes();

        list = list.stream().map((item)->{
            item.setSetmealId(setmealId);
            return item;
        }).collect(Collectors.toList());

        setmealDishService.saveBatch(list);
    }

    @Override
    public void deleteByIdWithDish(Long ids) {

        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId, ids);
        setmealDishService.remove(queryWrapper);


        LambdaQueryWrapper<Setmeal> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.eq(ids !=null, Setmeal::getId, ids);
        this.remove(queryWrapper1);

    }


}
