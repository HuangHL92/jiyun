package com.ruoyi.common.echarts.chart;

import com.ruoyi.common.echarts.data.SeriesData;
import com.ruoyi.common.echarts.json.GsonOption;
import org.apache.poi.ss.formula.functions.T;

public abstract class Chart<T>{

    //图表选项
    protected GsonOption option;

    //标题
    public String title;
    public T title(String title){
        this.title = title;
        return (T) this;
    }
    //事件
    public String click;
    public T click(String click){
        this.click = click;
        return  (T) this;
    }

    //坐标项
    public String[] category;
    public T category(String[] category){
        this.category = category;
        return (T)this;
    }
    //值
    public SeriesData[] seriesData;
    public T seriesData(SeriesData[] seriesData){
        this.seriesData = seriesData;
        return (T)this;
    }
    public abstract String toString();
}
