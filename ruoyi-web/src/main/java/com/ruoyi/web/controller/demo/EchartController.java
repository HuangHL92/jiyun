package com.ruoyi.web.controller.demo;

import com.ruoyi.common.echarts.axis.CategoryAxis;
import com.ruoyi.common.echarts.axis.ValueAxis;
import com.ruoyi.common.echarts.chart.BarChart;
import com.ruoyi.common.echarts.chart.LineChart;
import com.ruoyi.common.echarts.chart.PieChart;
import com.ruoyi.common.echarts.code.Symbol;
import com.ruoyi.common.echarts.code.Trigger;
import com.ruoyi.common.echarts.data.Data;
import com.ruoyi.common.echarts.data.LineData;
import com.ruoyi.common.echarts.data.SeriesData;
import com.ruoyi.common.echarts.json.GsonOption;
import com.ruoyi.common.echarts.series.Line;
import com.ruoyi.common.echarts.series.Series;
import com.ruoyi.framework.web.base.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import sun.management.counter.perf.PerfInstrumentation;

import java.util.Objects;
import java.util.UUID;

/**
 * echart实例demo
 *
 * @author ruoyi
 */
@Controller
@RequestMapping("/demo/echart")
public class EchartController extends BaseController {

    private String prefix = "demo";

    @GetMapping()
    public String echart(ModelMap mmap)
    {
        //饼图
        createPie(mmap);
        //柱状图
        createBar(mmap);
        createYBar(mmap);
        createStackBar(mmap);

        //线形图
        createLine(mmap);
        return prefix + "/echart";
    }

    /*
    * 饼图示例，
    * */
    private void createPie(ModelMap mmap)
    {
        //饼图
        //定义图表中的项及值
        ModelMap datas = new ModelMap();
        datas.put("直接访问",335);
        datas.put("邮件营销",310);
        datas.put("联盟广告",234);
        datas.put("视频广告",135);
        datas.put("搜索引擎",150);

        PieChart pie = new PieChart();
        pie.title("某站点用户访问来源").datas(datas).click("pieClick").toString();

        String strPie = pie.toString();
        mmap.put("pieOption",strPie);
    }

    /**
     *柱形图
     */
    private void createBar(ModelMap mmap)
    {
        //x轴
        String[] category = new String[]{"2012", "2013", "2014", "2015", "2016"};
        //y轴值
        SeriesData[] datas = new SeriesData[]{
                new SeriesData("Forest",new Object[]{320, 332, 301, 334, 390}),
                new SeriesData("Steppe",new Object[]{220, 182, 191, 234, 290})
        };

        BarChart bar = new BarChart();
        bar.category(category).seriesData(datas).click("barClick");
        String strPie = bar.toString();
        mmap.put("barOption",strPie);
    }

    /**
     *堆叠柱状图，
     */
    private void createStackBar(ModelMap mmap)
    {
        //x轴
        String[] category = new String[]{"周一","周二","周三","周四","周五","周六","周日"};
        //y轴值
        SeriesData[] datas = new SeriesData[]{
                new SeriesData("直接访问",new Object[]{320, 332, 301, 334, 390, 330, 320}),
                new SeriesData("视频广告","广告",new Object[]{150, 232, 201, 154, 190, 330, 410}),
                new SeriesData("联盟广告","广告",new Object[]{220, 182, 191, 234, 290, 330, 310})
        };

        BarChart bar = new BarChart();
        bar.category(category).seriesData(datas).click("barClick");
        String strPie = bar.toString();
        mmap.put("stackBarOption",strPie);
    }

    /**
     *柱形图
     */
    private void createYBar(ModelMap mmap)
    {
        //x轴
        String[] category = new String[]{"2012", "2013", "2014", "2015", "2016"};
        //y轴值
        SeriesData[] datas = new SeriesData[]{
                new SeriesData("Forest",new Object[]{10, 20, 30, 40, 50}),
        };

        BarChart bar = new BarChart();
        bar.category(category).seriesData(datas).isVertical(false).click("barClick");
        String strPie = bar.toString();
        mmap.put("ybarOption",strPie);
    }

    /**
     *线形图
     */
    private void createLine(ModelMap mmap)
    {
        //x轴
        String[] category = new String[]{"周一","周二","周三","周四","周五","周六","周日"};
        //y轴值
        SeriesData[] datas = new SeriesData[]{
                new SeriesData("邮件营销",new Object[]{120, 132, 101, 134, 90, 230, 210}),
                new SeriesData("联盟广告",new Object[]{220, 182, 191, 234, 290, 330, 310})
        };

        LineChart line = new LineChart();
        line.title("").category(category).seriesData(datas).click("lineClick");
        String strPie = line.toString();
        mmap.put("lineOption",strPie);
    }
}
