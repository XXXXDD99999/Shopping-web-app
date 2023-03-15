package com.xxd.platform.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xxd.platform.common.R;
import com.xxd.platform.dto.DishDto;
import com.xxd.platform.entity.Category;
import com.xxd.platform.entity.Dish;
import com.xxd.platform.service.CategoryService;
import com.xxd.platform.service.DishFlavorService;
import com.xxd.platform.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {

    @Autowired
    private DishService dishService;
    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RedisTemplate redisTemplate;

    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        //check redis, clear key
        String key = "dish_" + dishDto.getCategoryId();
        redisTemplate.delete(key);

        log.info(dishDto.toString());
        dishService.saveWithFlavor(dishDto);
        return R.success("save dish successful");
    }

    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){

        //fen ye gou zai qi
        Page<Dish> pageInfo = new Page<>(page, pageSize);
        Page<DishDto> dishDtoInfo = new Page<>(page,pageSize);

        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.like(name!= null, Dish::getName, name);
        queryWrapper.orderByDesc(Dish::getUpdateTime);

        dishService.page(pageInfo, queryWrapper);
        BeanUtils.copyProperties(pageInfo,dishDtoInfo, "records");

        List<Dish> records = pageInfo.getRecords();

        List<DishDto> list = records.stream().map((item) ->{
            DishDto dishDto = new DishDto();

            BeanUtils.copyProperties(item, dishDto);

            Long categoryId = item.getCategoryId();

            if(categoryId != null){
                //get category by id
                Category category = categoryService.getById(categoryId);

                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }


            return dishDto;
        }).collect(Collectors.toList());

        dishDtoInfo.setRecords(list);
        return R.success(dishDtoInfo);
    }

    /*
    * get dish information by id
    * */
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id){
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }

    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        String key = "dish_" + dishDto.getCategoryId();
        redisTemplate.delete(key);

        dishService.updateWithFlavor(dishDto);

        return R.success("update dish success");
    }

    @GetMapping("/list")
    public R<List<Dish>> getList(Dish dish){
        List<Dish> list = null;
        //search redis, if key is exist, return redis value
        String key = "dish_" + dish.getCategoryId();
        list = (List<Dish>) redisTemplate.opsForValue().get(key);
        if(list !=null){
            return R.success(list);
        }
        //else, search sql, and put it to the redis


        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        list = dishService.list(queryWrapper);

        //save in redis
        redisTemplate.opsForValue().set(key, list, 60, TimeUnit.MINUTES);
        return R.success(list);
    }

}
