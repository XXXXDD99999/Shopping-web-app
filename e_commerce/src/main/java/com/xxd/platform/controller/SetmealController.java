package com.xxd.platform.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xxd.platform.common.R;
import com.xxd.platform.dto.SetmealDto;
import com.xxd.platform.entity.Category;
import com.xxd.platform.entity.Setmeal;
import com.xxd.platform.service.CategoryService;
import com.xxd.platform.service.SetmealDishService;
import com.xxd.platform.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealController {
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private RedisTemplate redisTemplate;

    @PostMapping
    public R<String> saveWithDish(@RequestBody SetmealDto setmealDto){
        String key = "setmeal_" + setmealDto.getCategoryId();
        redisTemplate.delete(key);

        log.info(setmealDto.toString());

        setmealService.saveWithDishes(setmealDto);
        return R.success("save setmeal success");
    }


    @GetMapping("/page")
    public R<Page> pageShow(int page, int pageSize, String name){
        Page<Setmeal> pageInfo = new Page<>(page, pageSize);

        Page<SetmealDto> setmealDtoPage = new Page<>();



        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(name != null, Setmeal::getName, name);
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        setmealService.page(pageInfo, queryWrapper);

        BeanUtils.copyProperties(pageInfo, setmealDtoPage);

        List<Setmeal> records = pageInfo.getRecords();

        List<SetmealDto> setmealDtos = records.stream().map((item) ->{
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item, setmealDto);

            Long categoryId = item.getCategoryId();

            if(categoryId != null){
                Category category = categoryService.getById(categoryId);
                String categoryName = category.getName();
                setmealDto.setCategoryName(categoryName);
            }
            return setmealDto;
        }).collect(Collectors.toList());

        setmealDtoPage.setRecords(setmealDtos);
        return R.success(setmealDtoPage);
    }


    @GetMapping("/{id}")
    public R<SetmealDto> get(@PathVariable Long id){
        SetmealDto setmealDto = setmealService.getByIdWithDish(id);
        return R.success(setmealDto);
    }

    @PutMapping
    public R<String> update(@RequestBody SetmealDto setmealDto){
        String key = "setmeal_" + setmealDto.getCategoryId();
        redisTemplate.delete(key);

        setmealService.updateWithDish(setmealDto);

        return R.success("update setmeal success");
    }



    @DeleteMapping
    public  R<String> delete(@RequestParam List<Long> ids){


        for(int i = 0; i<ids.size();i++) {
            Long id = ids.get(i);
            Setmeal setmeal = setmealService.getById(id);
            int status = setmeal.getStatus();
            if (status == 1) {
                return R.error("change status to sold_out, then delete the setmeal");
            }
            setmealService.deleteByIdWithDish(id);
        }
            return R.success("delete setmeal success");

    }


//    @PostMapping("/status")
//    public R<String> changeStatus(@RequestParam Long id){
//        setmealService.updateById();
//
//        return R.success("change the setmeal status");
//    }

    @GetMapping("/list")
    public R<List<Setmeal>> getList(Setmeal setmeal){
        List<Setmeal> list = null;

        String key = "setmeal_" + setmeal.getCategoryId();
        list = (List<Setmeal>) redisTemplate.opsForValue().get(key);
        if(list != null){
            return R.success(list);
        }

        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(setmeal.getCategoryId()!=null, Setmeal::getCategoryId,setmeal.getCategoryId());
        queryWrapper.eq(setmeal.getStatus() != null,Setmeal::getStatus,setmeal.getStatus());
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        list = setmealService.list(queryWrapper);

        redisTemplate.opsForValue().set(key, list, 60, TimeUnit.MINUTES);
        return R.success(list);
    }

}
