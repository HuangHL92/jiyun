package com.ruoyi.web.controller.demo;

import com.ruoyi.framework.web.base.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 地图实例demo
 *
 * @author ruoyi
 */
@Controller
@RequestMapping("/demo/baidu")
public class MapController extends BaseController {

    private String prefix = "demo";

    @GetMapping()
    public String baidu()
    {
        return prefix + "/baidu";
    }
}
