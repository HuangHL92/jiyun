package com.ruoyi.web.controller.screen;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value={"/screen","/screen/a"})
public class SOneController {

    private String prefix = "screen/a";
    /**
     * 大屏首页
     */
    @GetMapping("")
    public String index(ModelMap mmap)
    {
        return prefix + "/index";
    }
}
