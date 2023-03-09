package com.xxd.platform.dto;

import com.xxd.platform.entity.Setmeal;
import com.xxd.platform.entity.SetmealDish;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
