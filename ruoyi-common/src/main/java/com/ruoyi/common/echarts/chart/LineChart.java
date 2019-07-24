package com.ruoyi.common.echarts.chart;

import cn.hutool.core.util.StrUtil;
import com.ruoyi.common.echarts.Config;
import com.ruoyi.common.echarts.axis.CategoryAxis;
import com.ruoyi.common.echarts.axis.ValueAxis;
import com.ruoyi.common.echarts.code.Orient;
import com.ruoyi.common.echarts.code.Trigger;
import com.ruoyi.common.echarts.code.X;
import com.ruoyi.common.echarts.data.Data;
import com.ruoyi.common.echarts.data.SeriesData;
import com.ruoyi.common.echarts.json.GsonOption;
import com.ruoyi.common.echarts.series.Bar;
import com.ruoyi.common.echarts.series.Line;

public class LineChart extends Chart<LineChart>{

    public LineChart(){

    }
    @Override
    public String toString() {
        option = new GsonOption();
        //点击事件
        if(!StrUtil.isEmpty(click)){
            option.click(click);
        }
        if(!StrUtil.isEmpty(title)) {
            option.title().text(title)
                    .x(X.center);
        }
        option.tooltip().trigger(Trigger.axis);
        option.toolbox().show(true);
        option.calculable(true);
        option.legend().orient(Orient.horizontal).x("right").y("top").show();

        option.xAxis(new CategoryAxis().boundaryGap(true).data(category));
        option.yAxis(new ValueAxis());

        option.grid().left("2%").right("2%").bottom("2%").top("10%").containLabel(true);
        option.color(Config.color);
        for(SeriesData d : seriesData)
        {
            Line line = new Line().smooth(true).stack(d.name()).name(d.name());
            Object[] values = d.values();
            for(Object value :values)
            {
                line.data(value);
            }
            option.series(line);
        }
        return  option.toString();
    }
}

