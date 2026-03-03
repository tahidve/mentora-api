package vn.mentora.mentora_api.controller;

import org.springframework.web.bind.annotation.RestController;

import vn.mentora.mentora_api.util.annotation.ApiMessage;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;

@RestController
public class TestController {

    @GetMapping("/test")
    @ApiMessage("Test API thành công!")
    public Map<String, String> test() {
        return Map.of("hello", "Mentora!");
    }

}
