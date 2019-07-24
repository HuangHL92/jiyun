package com.ruoyi.common.echarts.chart;
import cn.hutool.core.util.StrUtil;
import com.ruoyi.common.echarts.Config;
import com.ruoyi.common.echarts.Label;
import com.ruoyi.common.echarts.axis.CategoryAxis;
import com.ruoyi.common.echarts.axis.ValueAxis;
import com.ruoyi.common.echarts.data.Data;
import com.ruoyi.common.echarts.data.LineData;
import com.ruoyi.common.echarts.data.SeriesData;
import com.ruoyi.common.echarts.json.GsonOption;
import com.ruoyi.common.echarts.series.Bar;
import com.ruoyi.common.echarts.series.Line;
import com.ruoyi.common.echarts.series.Pie;
import com.ruoyi.common.echarts.series.Series;
import com.ruoyi.common.echarts.style.ItemStyle;
import org.springframework.http.HttpStatus;
import org.springframework.ui.ModelMap;
import com.ruoyi.common.echarts.code.*;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;


/**饼图展示
 * add by sww
 * at 2019-02-23
 * **/
public class BarChart extends Chart<BarChart> {

    //标识，水平还是垂直柱形图
    private boolean isVertical = true;
    public BarChart isVertical(boolean isVertical){
        this.isVertical = isVertical;
        return this;
    }

    public BarChart(){

    }
    @Override
    public String toString() {
        option = new GsonOption();
        if(!StrUtil.isEmpty(title)) {
            option.title().text(title)
                    .x(X.center);
        }
        //点击事件
        if(!StrUtil.isEmpty(click)){
            option.click(click);
        }
        option.tooltip().trigger(Trigger.axis);
        option.toolbox().show(true);
        option.calculable(true);
        option.legend().orient(Orient.horizontal).x("right").y("top").show();
        if(isVertical) {
            option.xAxis(new CategoryAxis().boundaryGap(true).data(category));
            option.yAxis(new ValueAxis());
        }else
        {
            option.yAxis(new CategoryAxis().boundaryGap(true).data(category));
            option.xAxis(new ValueAxis());
        }

        option.grid().left("2%").right("2%").bottom("2%").top("10%").containLabel(true);
        option.color(Config.color);

        for(SeriesData d : seriesData)
        {
            String stack = d.stack();
            if(StrUtil.isEmpty(stack))
            {
                stack = d.name();
            }
            Bar bar = new Bar().barWidth("10").stack(stack).name(d.name());
            Object[] values = d.values();
            for(Object value :values)
            {
                bar.data(value);
            }
            option.series(bar);
        }
        return  option.toString();
    }
}
