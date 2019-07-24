package com.ruoyi.web.controller.tool;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.google.common.collect.Lists;
import com.ruoyi.common.base.AjaxResult;
import com.ruoyi.common.config.Global;
import com.ruoyi.common.page.TableDataInfo;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.framework.util.ShiroUtils;
import com.ruoyi.framework.web.base.BaseController;
import com.ruoyi.tool.table.domain.GenTable;
import com.ruoyi.tool.table.domain.GenTableColumn;
import com.ruoyi.tool.table.domain.ViewTableColumn;
import com.ruoyi.tool.table.service.IGenTableService;
import org.apache.poi.ss.usermodel.Font;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * @author: xjm
 * @date: 2019/05/23
 */
@Controller
@RequestMapping("/tool/table/data")
public class TableDataController extends BaseController {

    private String prefix = "tool/table/data";
    @Autowired
    IGenTableService genTableService;

    @GetMapping("/{tableId}")
    public String tableData(@PathVariable String tableId, ModelMap mmap) {
        // 使用tableId而不是tableName，因为tableName可变，数据跟着配置表（gen_table）走
        mmap.put("tableId", tableId);
        return prefix + "/data";
    }

    @PostMapping("/{tableId}")
    @ResponseBody
    public AjaxResult tableInfo(@PathVariable String tableId) {
        GenTable table = genTableService.getById(tableId);
        return success().put("data", table);
    }

    @GetMapping("/columns")
    @ResponseBody
    public AjaxResult dynamicHeaders(String tableId) {
        AjaxResult success = success();

        GenTable genTable = genTableService.selectTableById(tableId);
        List<GenTableColumn> columns = genTable.getColumnList();  // 不包含删除的column，如要包含使用getAllColumnList
        Iterator<GenTableColumn> iterator = columns.iterator();
        List<ViewTableColumn> viewColumns = Lists.newArrayList();

        while (iterator.hasNext()) {
            ViewTableColumn viewColumn = new ViewTableColumn();
            GenTableColumn column = iterator.next();

            // 设置字段显示宽度
            String length = column.getLength();
            viewColumn.setWidth(100);  // longtext longblob double datetime 这些类型的字段默认显示宽度100
            if (StringUtils.isNotEmpty(length)) {
                if ((Object) length instanceof Integer) {
                    viewColumn.setWidth(Integer.valueOf(length) >= 300 ? 100 : Integer.valueOf(length));
                }
            }

            viewColumn.setTitle(column.getComments());  // 注释是必填的，所以一定会有title
//            String camelCaseStr = StringUtils.convertToCamelCase(column.getName());
//            viewColumn.setField(camelCaseStr.replaceFirst(camelCaseStr.substring(0, 1), camelCaseStr.substring(0, 1).toLowerCase()));
            viewColumn.setField(column.getName());
            viewColumn.setVisible(GenTableColumn.YES.equals(column.getIsPk()) ? false : true);  // 主键隐藏
            viewColumn.setAlign("center");
            viewColumns.add(viewColumn);
        }

        ViewTableColumn viewColumn;

        // 默认主键 - id
        viewColumn = new ViewTableColumn();
        viewColumn.setTitle("编号");
        viewColumn.setWidth(100);
        viewColumn.setVisible(false);
        viewColumn.setAlign("left");
        viewColumn.setField(GenTableColumn.PK_NAME);
        viewColumns.add(0, viewColumn);

        // 创建时间
        viewColumn = new ViewTableColumn();
        viewColumn.setTitle("创建时间");
        viewColumn.setWidth(100);
        viewColumn.setVisible(true);
        viewColumn.setAlign("left");
        viewColumn.setField("create_time");
        viewColumns.add(viewColumn);

        success.put("data", viewColumns);

        return success;
    }

    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo dynamicList(String tableId) {
        GenTable genTable = genTableService.selectTableById(tableId);
        return getDataTable(genTableService.searchTableData(genTable.getName()));
    }

    @GetMapping("/add")
    public String add(@RequestParam("tableId") String tableId, ModelMap mmap) {
        GenTable genTable = genTableService.selectTableById(tableId);
        List<GenTableColumn> columnList = genTable.getColumnList();

        // 移除主键
        Iterator<GenTableColumn> iterator = columnList.iterator();
        while (iterator.hasNext()) {
            GenTableColumn column = iterator.next();
            if (GenTableColumn.YES.equals(column.getIsPk())) {
                iterator.remove();
            }
        }

        mmap.put("formAction", prefix + "/add");
        mmap.put("tableId", genTable.getId());
        mmap.put("columnList", columnList);
        return prefix + "/add";
    }

    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(HttpServletRequest request) {
        return toAjax(genTableService.addTableData(request.getParameterMap()));
    }

    @GetMapping("/edit")
    public String edit(@RequestParam("id") String id, ModelMap mmap,
                       @RequestParam("tableId") String tableId) {
        GenTable genTable = genTableService.selectTableById(tableId);
        List<GenTableColumn> columnList = genTable.getColumnList();

        // 移除主键
        Iterator<GenTableColumn> iterator = columnList.iterator();
        while (iterator.hasNext()) {
            GenTableColumn column = iterator.next();
            if (GenTableColumn.YES.equals(column.getIsPk())) {
                iterator.remove();
            }
        }

        // 查询数据
        Object data = genTableService.searchDataById(genTable.getName(), id);

        mmap.put("formAction", prefix + "/edit");
        mmap.put("tableId", genTable.getId());
        mmap.put("columnList", columnList);
        mmap.put("data", data);

        return prefix + "/add";
    }

    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(HttpServletRequest request) {
        return toAjax(genTableService.updateTableData(request.getParameterMap(), ShiroUtils.getUserId()));
    }

    @PostMapping("/remove")
    @ResponseBody
    public AjaxResult remove(String ids, @RequestParam("tableId") String tableId) {
        return toAjax(genTableService.deleteTableData(ids, tableId));
    }


    /**
     * 导入模板下载
     * @param tableId
     * @param response
     * @return
     */
    @GetMapping("/export")
    @ResponseBody
    public AjaxResult exportTemplate(@RequestParam("tableId") String tableId, HttpServletResponse response) {
        // 根据sourceId取得对应的字段配置(列表结构）
        GenTable table = genTableService.selectTableById(tableId);
        List<GenTableColumn> clist = table.getColumnList();

        String fileName = "模板_" +  table.getName()+ "_" + RandomUtil.randomNumbers(4) +  ".xlsx";
        String filePath = Global.getDownloadPath() +  fileName;
        ExcelWriter writer = ExcelUtil.getWriter(filePath);

        List<String> nameCns = new ArrayList<>();
        for (GenTableColumn co :
                clist) {
            nameCns.add(co.getComments());
            writer.addHeaderAlias(co.getName(), co.getComments());
        }
        //设置字体
        Font font = writer.createFont();
        font.setBold(true);
        //第二个参数表示是否忽略头部样式
        writer.getStyleSet().setFont(font, true);
        for (int i =0;i<clist.size();i++){
            //设置宽度
            writer.setColumnWidth(i,25);
        }
        List<String> row1 = CollUtil.newArrayList(nameCns.iterator());
        List<List<String>> rows = CollUtil.newArrayList(Collections.singleton(row1));
        writer.write(rows);
        // 关闭writer，释放内存
        writer.close();

        return AjaxResult.success(fileName);
    }


    @PostMapping("/import")
    @ResponseBody
    public AjaxResult importData(String tableId, MultipartFile file) throws Exception {
        //读取excel
        ExcelReader reader = ExcelUtil.getReader(file.getInputStream());
        List<Map<String, Object>> list = new ArrayList<>();
        list = reader.readAll();
        if (list.size() == 0) {
            return AjaxResult.error("导入失败！原因：没有数据！");
        }

        String msg = genTableService.importData(list, tableId);

        if (StringUtils.isNotEmpty(msg)) {
            return AjaxResult.success(msg);
        } else {
            return AjaxResult.error("导入失败，没有数据被导入");
        }

    }


}
