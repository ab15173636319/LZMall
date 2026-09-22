package org.lzmweb.controller;

import org.lzmcommon.result.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class CheckController {

    @GetMapping("/")
    public Result<String> index() {
        return Result.success("你好");
    }

    @PostMapping("/postTest")
    public Result<String> postTest() {
        return Result.success("你好");
    }

    @GetMapping("/checkLink")
    public Result<String> test() {
        return Result.success("服务连接成功", "连接成功！");
    }

}
