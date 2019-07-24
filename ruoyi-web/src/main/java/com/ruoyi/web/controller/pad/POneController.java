package com.ruoyi.web.controller.pad;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value={"/pad","/pad/a"})
public class POneController {

    private String prefix = "pad/a";
    /**
     * pad首页
     */
    @GetMapping("")
    public String index(ModelMap mmap)
    {
        return prefix + "/index";
    }
}
