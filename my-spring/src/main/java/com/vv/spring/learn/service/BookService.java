package com.vv.spring.learn.service;

import com.vv.spring.learn.spring.Component;

@Component
public class BookService extends BaseService{

    @Override
    public void log() {
        System.out.println("我是bookService");
    }
}
