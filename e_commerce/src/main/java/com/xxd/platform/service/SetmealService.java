package com.xxd.platform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xxd.platform.dto.SetmealDto;
import com.xxd.platform.entity.Setmeal;
import com.xxd.platform.utils.SmsRequest;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface SetmealService extends IService<Setmeal> {
    void saveWithDishes(SetmealDto setmealDto);

    SetmealDto getByIdWithDish(Long id);

    void updateWithDish(SetmealDto setmealDto);


    void deleteByIdWithDish(Long ids);

    interface SmsSender {
        void sendSms(SmsRequest smsRequest);
    }
}

