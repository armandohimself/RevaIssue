package com.abra.revaissue.controller;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RevaIssueController {
    // So hitting "/" in the browser doesn't 404
    @GetMapping("/")
    public String apiRoot() {
        return "API is running. Try /health";
    }

    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("ok", true);
        out.put("time", new Date().toString());
        return out;
    }
}