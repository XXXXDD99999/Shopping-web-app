package com.xxd.platform.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xxd.platform.common.R;
import com.xxd.platform.entity.User;
import com.xxd.platform.service.UserService;
import com.xxd.platform.utils.SmsRequest;
import com.xxd.platform.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;

    @PostMapping("sendMsg")
    public R<String> sendSms(@RequestBody User user, HttpSession session){
        //get phone number
        String phone = user.getPhone();

        if(StringUtils.isNotEmpty(phone)){
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("code = {}", code);

            //调用阿里云提供的短信服务API完成发送短信
            //SMSUtils.sendMessage("瑞吉外卖","",phone,code);

            //session.setAttribute(phone, code);

            //store code in redis, and set ttl is 5 mins
            redisTemplate.opsForValue().set(phone, code, 300, TimeUnit.SECONDS);

            return R.success("send phone");
        }

        return R.error("send fail");

    }

    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session){
        log.info(map.toString());

        String phone = map.get("phone").toString();

        String code = map.get("code").toString();


        //Object codeInSession = session.getAttribute(phone);

        Object codeInRedis = redisTemplate.opsForValue().get(phone);

        if(codeInRedis != null && codeInRedis.equals(code)){
            //如果能够比对成功，说明登录成功



            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone,phone);

            User user = userService.getOne(queryWrapper);
            if(user == null){
                //判断当前手机号对应的用户是否为新用户，如果是新用户就自动完成注册
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            session.setAttribute("user",user.getId());

            // del code in redis
            redisTemplate.delete(phone);

            return R.success(user);
        }
        return R.error("登录失败");

    }
}
