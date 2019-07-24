package com.ruoyi.common.echarts.chart;

import cn.hutool.core.util.StrUtil;
import com.ruoyi.common.echarts.Config;
import com.ruoyi.common.echarts.Label;
import com.ruoyi.common.echarts.data.Data;
import com.ruoyi.common.echarts.json.GsonOption;
import com.ruoyi.common.echarts.series.Pie;
import com.ruoyi.common.echarts.style.ItemStyle;
import org.springframework.ui.ModelMap;
import com.ruoyi.common.echarts.code.*;

import java.util.ArrayList;
import java.util.Objects;
/**饼图展示
 * add by sww
 * at 2019-02-21
 * **/
public class PieChart extends  Chart<PieChart>{

    //数值及列
    private ModelMap datas;
    public  PieChart datas(ModelMap datas){
        this.datas = datas;
        return this;
    }

    public PieChart()
    {
    }
    @Override
    public String toString() {
        ItemStyle dataStyle = new ItemStyle();
        dataStyle.normal().label(new Label().show(false)).labelLine().show(false);

        option = new GsonOption();
        //标题
        if(!StrUtil.isEmpty(title)) {
            option.title().text(title)
                    .x(X.center);
        }
        //点击事件
        if(!StrUtil.isEmpty(click)){
            option.click(click);
        }
        option.legend().orient(Orient.vertical).left("2").bottom("2").show();
        option.tooltip().show(true).trigger(Trigger.item).formatter("{b} : {c} ({d}%)");
        option.color(Config.color);
        Pie pie = new Pie("");
        pie.radius("60%").center("60%","50%").itemStyle(dataStyle);
        //数组
        for(String key : datas.keySet())
        {
            pie.data(new Data(key,datas.get(key)));
        }
        option.series(pie);
        return  option.toString();
    }
}
