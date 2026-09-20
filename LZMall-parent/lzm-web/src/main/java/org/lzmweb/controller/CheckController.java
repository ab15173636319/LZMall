package org.lzmweb.controller;

import org.lzmcommon.result.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class CheckController {
    @GetMapping("/checkLink")
    public Result<String> test() {
        return Result.success("连接成功！");
    }

}
