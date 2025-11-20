package com.octal.supa.controller;


import com.octal.supa.model.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Controller
public class QuickBookSyncTemplateController {

    @GetMapping("/support")
    public String support(HttpServletRequest request) {
        try {
            return "support.html";
        } catch (Exception o) {
            return "support";
        }
    }
}
