package com.xxd.platform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xxd.platform.dto.DishDto;
import com.xxd.platform.entity.Dish;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface DishService extends IService<Dish> {
    public void saveWithFlavor(DishDto dishDto);

    // get falvour by dish id
    public DishDto getByIdWithFlavor(Long id);

    public void updateWithFlavor(DishDto dishDto);
}
