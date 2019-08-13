package com.ruoyi.web.controller.edu;

import java.util.Arrays;

import com.ruoyi.area.edu.domain.Tag;
import com.ruoyi.area.edu.service.ITagService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.framework.web.base.BaseController;
import com.ruoyi.common.page.TableDataInfo;
import com.ruoyi.common.base.AjaxResult;
import com.ruoyi.common.support.Convert;


/**
 * 标签 信息操作处理
 *
 * @author jiyunsoft
 * @date 2019-08-13
 */
@Controller
@RequestMapping("/edu/tag")
public class TagController extends BaseController {
    private String prefix = "edu/tag";

    @Autowired
    private ITagService tagService;

    @RequiresPermissions("edu:tag:view")
    @GetMapping()
    public String tag() {
        return prefix + "/list";
    }

    /**
     * 查询标签列表
     */
    @RequiresPermissions("edu:tag:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(Tag tag) {
        startPage();
        return getDataTable(tagService.selectList(tag));
    }

    /**
     * 新增标签
     */
    @GetMapping("/add")
    public String add(ModelMap mmap) {
        Tag tag = new Tag();
        //表单Action指定
        tag.setFormAction(prefix + "/add");
        mmap.put("tag", tag);
        return prefix + "/add";
    }

    /**
     * 新增保存标签
     */
    @RequiresPermissions("edu:tag:add")
    @Log(title = "标签管理", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(Tag tag) {
        return toAjax(tagService.save(tag));
    }

    /**
     * 修改标签
     */
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") String id, ModelMap mmap) {
        Tag tag = tagService.getById(id);
        //表单Action指定
        tag.setFormAction(prefix + "/edit");

        mmap.put("tag", tag);
        return prefix + "/add";
    }

    /**
     * 修改保存标签
     */
    @RequiresPermissions("edu:tag:edit")
    @Log(title = "标签管理", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(Tag tag) {

        return toAjax(tagService.saveOrUpdate(tag));
    }

    /**
     * 删除标签
     */
    @RequiresPermissions("edu:tag:remove")
    @Log(title = "标签管理", businessType = BusinessType.DELETE)
    @PostMapping("/remove")
    @ResponseBody
    public AjaxResult remove(String ids) {
        return toAjax(tagService.removeByIds(Arrays.asList(Convert.toStrArray(ids))));
    }


    /**
     * 更新显示顺序（Ajax）
     */
    @Log(title = "标签管理", businessType = BusinessType.UPDATE)
    @RequiresPermissions("edu:tag:edit")
    @GetMapping("/updateOrder")
    @ResponseBody
    public AjaxResult updateOrder(Tag tag) {
        try {
            tagService.updateById(tag);
        } catch (Exception ex) {
            return AjaxResult.error("更新失败！");
        }

        return AjaxResult.success();
    }

    /**
     * 客户端状态修改
     */
    @Log(title = "标签管理", businessType = BusinessType.UPDATE)
    @RequiresPermissions("edu:tag:edit")
    @PostMapping("/changeStatus")
    @ResponseBody
    public AjaxResult changeStatus(Tag tag) {
        return toAjax(tagService.updateById(tag));
    }
}
