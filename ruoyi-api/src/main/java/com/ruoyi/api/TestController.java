package com.ruoyi.api;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.area.demo.domain.Demo;
import com.ruoyi.area.demo.service.IDemoService;
import com.ruoyi.base.ApiBaseController;
import com.ruoyi.common.annotation.ValidateRequest;
import com.ruoyi.common.base.ApiResult;
import com.ruoyi.common.enums.DataSourceType;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.framework.datasource.DynamicDataSourceContextHolder;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;


@Api(value = "/test", description = "测试接口")
@RestController
@RequestMapping("/api/test/*")
public class TestController extends ApiBaseController {


    @Autowired
    private  IDemoService  demoService;

    @Autowired
    private JdbcTemplate jdbcTemplate;


    @ApiOperation("get测试")
    @GetMapping("get")
    public ApiResult get(String mobile)
    {
        //1.参数验证
        if(StringUtils.isEmpty(mobile)) {
            return ApiResult.error("参数错误");
        }

        return ApiResult.success("您输入的是：" + mobile);
    }

    @ApiOperation("get测试(V2)")
    @GetMapping("v2/get")
    public ApiResult get_v2(String mobile)
    {
        //1.参数验证
        if(StringUtils.isEmpty(mobile)) {
            return ApiResult.error("参数错误");
        }

        return ApiResult.success("您输入的是(V2)：" + mobile);
    }

    @ApiOperation("post测试")
    @PostMapping("post")
    public ApiResult post(String mobile)
    {
        //1.参数验证
        if(StringUtils.isEmpty(mobile) ) {
            return ApiResult.error("参数错误");
        }

        return ApiResult.success("您输入的是：" + mobile);
    }

    @ApiOperation(value = "请求验证测试",notes="请先调用/auth/login 接口取得token信息，并将token放入请求头部key为token")
    @GetMapping("validateRequest")
    @ValidateRequest
    public ApiResult validateRequest(String mobile)
    {
        //1.参数验证
        if(StringUtils.isEmpty(mobile)) {
            return ApiResult.error("参数错误");
        }

        return ApiResult.success("您输入的是：" + mobile);
    }

    @ApiOperation("异常模拟")
    @GetMapping("getException")
    public ApiResult get_exception()
    {

        int i=1/0;

        return ApiResult.success();
    }


    @ApiOperation("mybatisPlus 翻页获得数据Demo1")
    @GetMapping("/mptest4page1/{page}/{size}")
    public Map<String, Object> mptest4page1(@PathVariable Integer page, @PathVariable Integer size) {
        Map<String, Object> map = new HashMap<>();
        Page<Demo> questionStudent = demoService.selectList4Page1(new Page<>(page, size));
        if (questionStudent.getRecords().size() == 0) {
            map.put("code", 400);
        } else {
            map.put("code", 200);
            map.put("data", questionStudent);
        }
        return map;
    }

    @ApiOperation("mybatisPlus 多表翻页获得数据Demo2")
    @GetMapping("/mptest4page2/{page}/{size}/{keywords}")
    public Map<String, Object> mptest4page2(@PathVariable Integer page, @PathVariable Integer size,@RequestParam(name="keywords",required = false) String keywords) {
        Map<String, Object> map = new HashMap<>();
        Demo demo = new Demo();
        demo.setName(keywords);
        Page<Demo> list = demoService.selectList4Page2(new Page<>(page, size),demo);

        if (list.getRecords().size() == 0) {
            map.put("code", 400);
        } else {
            map.put("code", 200);
            map.put("data", list);
        }
        return map;
    }


    @ApiOperation("多数据源测试（1:数据源1 2:数据源2）")
    @GetMapping("/dds/{datasource}")
    public Map<String, Object> dds(@PathVariable(name="datasource") Integer datasource) {
        Map<String, Object> map = new HashMap<>();
        String ds = datasource==1?DataSourceType.MASTER.name():DataSourceType.SLAVE.name();

        //切换数据源
        DynamicDataSourceContextHolder.setDataSourceType(ds);
        Page<Demo> questionStudent = demoService.selectList4Page1(new Page<>(1, 100000));
        if (questionStudent.getRecords().size() == 0) {
            map.put("code", 400);
        } else {
            map.put("code", 200);
            map.put("data", questionStudent);
        }
        //清空数据源
        DynamicDataSourceContextHolder.clearDataSourceType();

        return map;
    }


    @ApiOperation("事务处理模拟测试（输入1可以模拟异常）")
    @GetMapping("/trans/{hasError}")
    @Transactional(rollbackFor = Exception.class)
    public ApiResult trans(@PathVariable(name="hasError") Integer hasError) {

        int i=1;
        String name  = RandomUtil.randomNumbers(6);

        //第1次插入
        Demo demo =new Demo();
        demo.setId(IdUtil.simpleUUID());
        demo.setName(name + "_" + i++);
        demoService.save(demo);

        //TODO 模式错误
        if(hasError==1) {
            i = 1/0;
        }

        //第2次插入
        demo.setId(IdUtil.simpleUUID());
        demo.setName(name + "_" + i++);
        demoService.save(demo);

        return ApiResult.success("提交成功！查看sys_demo表或综合实例列表，验证结果（插入2条记录）。");
    }


    @ApiOperation("批量插入测试(JdbcTemplate)")
    @GetMapping("/batchJdbc/{size}")
    public ApiResult batchJdbc(@PathVariable(name="size") Integer size) {

        String sql = "INSERT INTO sys_demo " +
                "(id, name) VALUES (?, ?)";

        List<Demo> list = new ArrayList();
        String name = "test_" + RandomUtil.randomNumbers(6);
        for(int i=0;i<size;i++) {
            Demo demo =new Demo();
            demo.setId(IdUtil.simpleUUID());
            demo.setName(name + "_" + i++);
            list.add(demo);
        }

        int[] rs = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setString(1, list.get(i).getId());
                ps.setString(2, list.get(i).getName());
            }
            @Override
            public int getBatchSize() {
                return list.size();
            }
        });

        return ApiResult.success("成功插入" + size + "条记录！" );
    }


    @ApiOperation("批量插入测试(MP)")
    @GetMapping("/batchmp/{size}")
    public ApiResult batchmp(@PathVariable(name="size") Integer size) {

        List<Demo> list = new ArrayList();
        String name = "test_" + RandomUtil.randomNumbers(6);
        for(int i=0;i<size;i++) {
            Demo demo =new Demo();
            demo.setId(IdUtil.simpleUUID());
            demo.setName(name + "_" + i++);
            list.add(demo);
        }

        demoService.saveBatch(list);

        return ApiResult.success("成功插入" + size + "条记录！");
    }


}
