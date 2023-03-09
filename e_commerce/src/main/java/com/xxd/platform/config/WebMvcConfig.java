package com.xxd.platform.config;

import com.xxd.platform.common.JacksonObjectMapper;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

//    @Override
//    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
//        registry.addResourceHandler("/backend/**").addResourceLocations("classpath:/static/backend");
//        registry.addResourceHandler("/frontend/**").addResourceLocations("classpath:/static/frontend");
//    }

    @Override
    /*
    * tuo zhan mvc xiaoxi zhuang huan qi
    * */
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        //create message converter
        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
        messageConverter.setObjectMapper(new JacksonObjectMapper());
        converters.add(0,messageConverter);
    }
}
