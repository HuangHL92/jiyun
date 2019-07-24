package com.ruoyi.api;

import com.ruoyi.area.demo.domain.Department;
import com.ruoyi.area.demo.service.IDepartmentService;
import com.ruoyi.base.ApiBaseController;
import com.ruoyi.common.annotation.DataSource;
import com.ruoyi.common.base.ApiResult;
import com.ruoyi.common.enums.DataSourceType;
import com.ruoyi.framework.datasource.DynamicDataSourceContextHolder;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 多数据源
 * @author eason
 * @date 2019/5/6 10:47
 */
@Api(value = "/dynamicDS", description = "多数据源测试接口")
@RestController
@RequestMapping("/api/dynamicDS")
public class DynamicDSController extends ApiBaseController {

    @Autowired
    private IDepartmentService departmentService;

    @ApiOperation("查询主库Master（默认）")
    @GetMapping("/getFromMaster")
    @DataSource(value = DataSourceType.MASTER)
    public ApiResult get()
    {
        List<Department> list = departmentService.list();
        return success(list);
    }

    @ApiOperation("查询从库Slave")
    @GetMapping("/getFromSlave")
    @DataSource(value = DataSourceType.SLAVE)
    public ApiResult getFromSlave()
    {
        List<Department> list = departmentService.list();
        return success(list);
    }

    @ApiOperation("查询从库Third")
    @GetMapping("/getFromThird")
    @DataSource(value = DataSourceType.THIRD)
    public ApiResult getFromThird()
    {
        List<Department> list = departmentService.list();
        return success(list);
    }

    @ApiOperation("一个方法内查询多个数据源")
    @GetMapping("/getFromAll")
    public ApiResult getFromAll()
    {
        DynamicDataSourceContextHolder.setDataSourceType(DataSourceType.MASTER.name());
        List<Department> master = departmentService.list();
        DynamicDataSourceContextHolder.setDataSourceType(DataSourceType.SLAVE.name());
        List<Department> slave = departmentService.list();
        DynamicDataSourceContextHolder.setDataSourceType(DataSourceType.THIRD.name());
        List<Department> third = departmentService.list();
        // 清空数据源变量
        DynamicDataSourceContextHolder.clearDataSourceType();
        Map result = new HashMap(3);
        result.put("master", master);
        result.put("slave", slave);
        result.put("third", third);
        return success(result);
    }

}
