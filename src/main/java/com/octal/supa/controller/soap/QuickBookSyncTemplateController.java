package com.octal.supa.controller.soap;


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
