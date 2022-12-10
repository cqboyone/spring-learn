package com.vv.spring.learn.service;

import com.vv.spring.learn.spring.BeanNameAware;
import com.vv.spring.learn.spring.Component;

@Component
public class UserService extends BaseService implements BeanNameAware {

    @Override
    public void log() {
        System.out.println("我是userService");
    }

    @Override
    public void setBeanName(String beanName) {
        System.out.println("设置beanName=" + beanName);
    }
}
