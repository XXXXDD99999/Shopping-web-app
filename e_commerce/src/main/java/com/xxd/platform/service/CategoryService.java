package com.xxd.platform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xxd.platform.entity.Category;

public interface CategoryService extends IService<Category> {
    public void remove(Long id);
}
