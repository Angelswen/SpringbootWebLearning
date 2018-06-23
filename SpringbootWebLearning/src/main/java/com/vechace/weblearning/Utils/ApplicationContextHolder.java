package com.vechace.weblearning.Utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Spring应用上下文：实现ApplicationContextAware接口，重点关注getBean方法
 * Created by vechace on 2018/6/23
 */
@Component
public class ApplicationContextHolder implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException{
        applicationContext = ctx;
    }

    public static ApplicationContext getApplicationContext(){
        return applicationContext;
    }

    public static <T> T getBean(Class<T> clazz){
        return applicationContext.getBean(clazz);
    }

    /**
     * Get bean by name form the applicationContext
     * @param name
     * @param <T>
     * @return
     */
    public static <T> T getBean(String name){
        return (T) applicationContext.getBean(name);
    }

}
