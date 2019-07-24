package com.ruoyi.web.controller.demo;


import com.ruoyi.area.demo.domain.Demo;
import com.ruoyi.area.demo.service.IDemoService;
import com.ruoyi.common.page.TableDataInfo;
import com.ruoyi.framework.web.base.BaseController;
import com.ruoyi.system.service.ISysPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/demo/catalog")
public class CatalogController extends BaseController {

    private String prefix = "demo/catalog";

    @Autowired
    private ISysPostService postService;
    @Autowired
    private IDemoService demoService;

    @GetMapping()
    public String demo() {
        return prefix + "/list";
    }

    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(Demo demo) {

        startPage();
        return getDataTable(demoService.selectList(demo));
    }

    /**
     * 新增测试
     */
    @GetMapping("/add")
    public String add(ModelMap mmap) {
        mmap.put("posts", postService.selectPostAll());
        Demo demo = new Demo();
        //表单Action指定
        demo.setFormAction(prefix + "/add");
        mmap.put("demo", demo);
        return prefix + "/add";
    }

    /**
     * 修改测试
     */
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") String id, ModelMap mmap) {
        Demo demo = demoService.getById(id);

        demo.setId(pk_encrypt(demo.getId()));
        //表单Action指定
        demo.setFormAction(prefix + "/edit");

        mmap.put("demo", demo);
        return prefix + "/add";
    }
}
