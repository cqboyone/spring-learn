package com.vv.spring.learn.service;

import com.vv.spring.learn.spring.Autowired;
import com.vv.spring.learn.spring.Component;

@Component("productService")
public class ProductService extends BaseService {

    @Autowired
    private BookService bookService;

    public void testBookService(){
        bookService.log();
    }
}
