package com.vv.spring.learn;

import com.vv.spring.learn.service.BookService;
import com.vv.spring.learn.service.ProductService;
import com.vv.spring.learn.service.UserService;
import com.vv.spring.learn.spring.MyApplicationContext;
import com.vv.spring.learn.spring.MyConfig;

public class Main {
    public static void main(String[] args) {
        Main main = new Main();
        main.testAware();
    }

    public void createBean() {
        MyApplicationContext applicationContext = new MyApplicationContext(MyConfig.class);
        UserService userService1 = (UserService) applicationContext.getBean("userService");
        UserService userService2 = (UserService) applicationContext.getBean("userService");
        ProductService productService = (ProductService) applicationContext.getBean("productService");
        BookService bookService = (BookService) applicationContext.getBean("bookService");
        System.out.println(userService1);
        userService1.log();
        System.out.println(userService2);
        userService2.log();
        System.out.println(productService);
        productService.log();
        System.out.println(bookService);
        bookService.log();
    }

    public void testAutowired() {
        MyApplicationContext applicationContext = new MyApplicationContext(MyConfig.class);
        ProductService productService = (ProductService) applicationContext.getBean("productService");
        productService.testBookService();
    }

    /**
     * 测试回调
     */
    public void testAware() {
        MyApplicationContext applicationContext = new MyApplicationContext(MyConfig.class);
    }
}