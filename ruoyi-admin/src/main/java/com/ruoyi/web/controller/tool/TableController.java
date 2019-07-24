package com.ruoyi.web.controller.tool;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.base.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.page.TableDataInfo;
import com.ruoyi.framework.web.base.BaseController;
import com.ruoyi.tool.table.domain.GenTable;
import com.ruoyi.tool.table.service.IGenTableService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author: xjm
 * @date: 2019/04/01
 */
@Controller
@RequestMapping("/tool/table")
public class TableController extends BaseController {

    @Autowired
    IGenTableService genTableService;

    private String prefix = "tool/table";

    @RequiresPermissions("tool:table:view")
    @GetMapping
    public String genTable() {
        return prefix + "/table";
    }

    @RequiresPermissions("tool:table:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(GenTable genTable) {
        startPage();
        List<GenTable> list = genTableService.selectAllList(genTable);
        return getDataTable(list);
    }

    @GetMapping("/add")
    public String add() {
        return prefix + "/add";
    }

    @RequiresPermissions("tool:table:add")
    @Log(title = "表单配置", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(GenTable genTable)
    {
        return toAjax(genTableService.insertTable(genTable));
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable String id, ModelMap modelMap) {
        GenTable genTable = genTableService.selectTableById(id);
        modelMap.put("genTable", genTable);
        return prefix + "/edit";
    }

    @RequiresPermissions("tool:table:edit")
    @Log(title = "表单配置", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(GenTable genTable) {
        return toAjax(genTableService.updateTable(genTable));
    }

    @RequiresPermissions("tool:table:remove")
    @Log(title = "表单配置", businessType = BusinessType.DELETE)
    @PostMapping("/remove")
    @ResponseBody
    public AjaxResult remove(String ids) {
        genTableService.removeTable(ids);
        return AjaxResult.success();
    }

    @RequiresPermissions("tool:table:delete")
    @Log(title = "表单配置", businessType = BusinessType.CLEAN)
    @PostMapping("/delete")
    @ResponseBody
    public AjaxResult delete(String ids) {
        genTableService.deleteTable(ids);
        return AjaxResult.success();
    }

    @RequiresPermissions("tool:table:synchDb")
    @Log(title = "表单配置", businessType = BusinessType.OTHER)
    @PostMapping("/synchDb")
    @ResponseBody
    public AjaxResult synchDB(String id,boolean isForce) {
        genTableService.synchDb(id,isForce);
        return AjaxResult.success();
    }

    @GetMapping("/checkTable")
    @ResponseBody
    public AjaxResult checkTableExist(@RequestParam String tableName,
                                      @RequestParam(required = false) String tableId) {
        GenTable genTable = new GenTable();
        genTable.setName(tableName);
        genTable.setId(tableId);
        // 表存在就返回error
        return toAjax(!genTableService.checkIsTableExist(genTable));
    }

}
