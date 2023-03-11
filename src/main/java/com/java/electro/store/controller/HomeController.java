package com.java.electro.store.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tests")
public class HomeController {

    @GetMapping
    public String testing(){
        return "welcome to electronic store";
    }
}
