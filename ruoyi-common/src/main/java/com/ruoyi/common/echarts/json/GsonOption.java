/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2015 abel533@gmail.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.ruoyi.common.echarts.json;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.ruoyi.common.echarts.Option;

import java.util.UUID;

/**
 * 增强的Option - 主要输出显示
 *
 * @author ***
 */
public class GsonOption extends Option {

    /**
     * 在浏览器中查看
     */
    public void view() {
        OptionUtil.browse(this);
    }

    private String click;
    public GsonOption click(String click) {
        this.click = click;
        return this;
    }

    @Override
    /**
     * 获取toString值
     */
    public String toString() {

        String strOption = GsonUtil.format(this);
        String uuid =   RandomUtil.randomNumbers(8);
        StringBuilder strReuslt = new StringBuilder();
        strReuslt.append(String.format("<div class=\"chart\" id=\"chart%s\">",uuid));
        strReuslt.append("</div>");
        strReuslt.append("<script type=\"text/javascript\">");
        strReuslt.append("setTimeout(function () {");
        strReuslt.append(String.format("var option%s =%s;",uuid,strOption));
        strReuslt.append(String.format("var ctrl%s= document.getElementById('chart%s');",uuid,uuid));
        strReuslt.append(String.format("option%s.backgroundColor ='';",uuid));
        strReuslt.append(String.format("var chart = echarts.init(ctrl%s);",uuid));
        strReuslt.append(String.format("chart.setOption(option%s);",uuid));
        if(!StrUtil.isEmpty(click)) {
            strReuslt.append("chart.on('click',function(param){" + click + "(param)});");
        }
        strReuslt.append("},1000);");
        strReuslt.append("</script>");
        return strReuslt.toString();
    }

    /**
     * 获取toPrettyString值
     */
    public String toPrettyString() {
        return GsonUtil.prettyFormat(this);
    }

    /**
     * 导出到指定文件名
     *
     * @param fileName
     * @return 返回html路径
     */
    public String exportToHtml(String fileName) {
        return exportToHtml(System.getProperty("java.io.tmpdir"), fileName);
    }

    /**
     * 导出到指定文件名
     *
     * @param fileName
     * @return 返回html路径
     */
    public String exportToHtml(String filePath, String fileName) {
        return OptionUtil.exportToHtml(this, filePath, fileName);
    }

}
