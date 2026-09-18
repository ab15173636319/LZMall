package org.lzmweb.controller;

import org.lzmservice.entity.Test;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/")
    public String test() {
        return "test";
    }


    @GetMapping("/obj")
    public String test2() {

        Test test = new Test();
        test.setName("张三");
        test.setGender("男");

        return test.toString();
    }

}
