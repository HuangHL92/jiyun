package com.ruoyi.web.controller.wap;

import com.ruoyi.framework.web.base.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


/**
 * 移动端
 *
 * @author jiyunsoft
 * @date 2019-02-22
 */
@Controller
@RequestMapping("/wap/demo")
public class WapDemoController extends BaseController {

    private String prefix = "wap/demo";

    /**
     * 首页
     *
     * @return
     */
    @GetMapping("")
    public String index() {
        return prefix + "/index";
    }

    /**
     * 基础-基础css类
     *
     * @return
     */
    @GetMapping("/base0")
    public String base0() {
        return prefix + "/base/base0";
    }

    /**
     * 基础-footer
     *
     * @return
     */
    @GetMapping("/base1")
    public String base1() {
        return prefix + "/base/base1";
    }

    /**
     * 基础-flex
     *
     * @return
     */
    @GetMapping("/base2")
    public String base2() {
        return prefix + "/base/base2";
    }

    /**
     * 基础-字体大小/9种颜色/背景
     *
     * @return
     */
    @GetMapping("/base3")
    public String base3() {
        return prefix + "/base/base3";
    }

    /**
     * 基础-边框/padding/margin/圆角/菜单竖线
     *
     * @return
     */
    @GetMapping("/base4")
    public String base4() {
        return prefix + "/base/base4";
    }

    /**
     * 基础-九宫格/十六宫格/无图宫格
     *
     * @return
     */
    @GetMapping("/base5")
    public String base5() {
        return prefix + "/base/base5";
    }

    /**
     * 基础-表格
     *
     * @return
     */
    @GetMapping("/base6")
    public String base6() {
        return prefix + "/base/base6";
    }

    /**
     * 基础-article/时间线
     *
     * @return
     */
    @GetMapping("/base7")
    public String base7() {
        return prefix + "/base/base7";
    }

    /**
     * 基础-加载提示/loading/分割线
     *
     * @return
     */
    @GetMapping("/base8")
    public String base8() {
        return prefix + "/base/base8";
    }

    /**
     * 基础-小红点/徽章
     *
     * @return
     */
    @GetMapping("/base9")
    public String base9() {
        return prefix + "/base/base9";
    }

    /**
     * 基础-图标
     *
     * @return
     */
    @GetMapping("/base10")
    public String base10() {
        return prefix + "/base/base10";
    }

    /**
     * 基础-标签
     *
     * @return
     */
    @GetMapping("/base11")
    public String base11() {
        return prefix + "/base/base11";
    }

    /**
     * 基础-gallery
     *
     * @return
     */
    @GetMapping("/base12")
    public String base12() {
        return prefix + "/base/base12";
    }

    /**
     * 基础-列表list
     *
     * @return
     */
    @GetMapping("/base13")
    public String base13() {
        return prefix + "/base/base13";
    }

    /**
     * 基础-面板
     *
     * @return
     */
    @GetMapping("/base14")
    public String base14() {
        return prefix + "/base/base14";
    }

    /**
     * 基础-动画
     *
     * @return
     */
    @GetMapping("/base15")
    public String base15() {
        return prefix + "/base/base15";
    }

    /**
     * 基础-头像认证/图片/图片排版
     *
     * @return
     */
    @GetMapping("/base16")
    public String base16() {
        return prefix + "/base/base16";
    }

    /**
     * 基础-按钮
     *
     * @return
     */
    @GetMapping("/base17")
    public String base17() {
        return prefix + "/base/base17";
    }

    /**
     * 基础-msg提示页
     *
     * @return
     */
    @GetMapping("/base18")
    public String base18() {
        return prefix + "/base/base18";
    }

    /**
     * 基础-折叠面板Collapse
     *
     * @return
     */
    @GetMapping("/base19")
    public String base19() {
        return prefix + "/base/base19";
    }

    /**
     * 基础-角标，图片展示
     *
     * @return
     */
    @GetMapping("/base20")
    public String base20() {
        return prefix + "/base/base20";
    }

    /**
     * 基础-js动态添加删除内容
     *
     * @return
     */
    @GetMapping("/table")
    public String table() {
        return prefix + "/base/table";
    }

    /**
     * 表单-单选,复选,开关
     *
     * @return
     */
    @GetMapping("/form1")
    public String form1() {
        return prefix + "/form/form1";
    }

    /**
     * 表单-文本框/文本域/验证码输入/验证表单
     *
     * @return
     */
    @GetMapping("/form2")
    public String form2() {
        return prefix + "/form/form2";
    }

    /**
     * 表单-支付场景
     *
     * @return
     */
    @GetMapping("/form3")
    public String form3() {
        return prefix + "/form/form3";
    }

    /**
     * 表单-picker/日期/时间/地址选择
     *
     * @return
     */
    @GetMapping("/form4")
    public String form4() {
        return prefix + "/form/form4";
    }

    /**
     * 表单-select
     *
     * @return
     */
    @GetMapping("/form5")
    public String form5() {
        return prefix + "/form/form5";
    }

    /**
     * 表单-popup/通知
     *
     * @return
     */
    @GetMapping("/form6")
    public String form6() {
        return prefix + "/form/form6";
    }

    /**
     * 表单-计数器
     *
     * @return
     */
    @GetMapping("/form7")
    public String form7() {
        return prefix + "/form/form7";
    }

    /**
     * 表单-搜索
     *
     * @return
     */
    @GetMapping("/form8")
    public String form8() {
        return prefix + "/form/form8";
    }

    /**
     * 表单-星级评分
     *
     * @return
     */
    @GetMapping("/form9")
    public String form9() {
        return prefix + "/form/form9";
    }

    /**
     * 表单-滑块/slider
     *
     * @return
     */
    @GetMapping("/form10")
    public String form10() {
        return prefix + "/form/form10";
    }

    /**
     * 表单-表单预览
     *
     * @return
     */
    @GetMapping("/form11")
    public String form11() {
        return prefix + "/form/form11";
    }

    /**
     * 表单-图片上传和预览,压缩
     *
     * @return
     */
    @GetMapping("/form12")
    public String form12() {
        return prefix + "/form/form12";
    }

    /**
     * 表单-单选,复选,文本框,文本域美化版
     *
     * @return
     */
    @GetMapping("/form13")
    public String form13() {
        return prefix + "/form/form13";
    }

    /**
     * 表单-上传进度
     *
     * @return
     */
    @GetMapping("/form14")
    public String form14() {
        return prefix + "/form/form14";
    }

    /**
     * 组件-对话框/toptip/toast
     *
     * @return
     */
    @GetMapping("/js1")
    public String js1() {
        return prefix + "/zujian/js1";
    }

    /**
     * 组件-actionsheet
     *
     * @return
     */
    @GetMapping("/js2")
    public String js2() {
        return prefix + "/zujian/js2";
    }

    /**
     * 组件-滑动删除
     *
     * @return
     */
    @GetMapping("/js3")
    public String js3() {
        return prefix + "/zujian/js3";
    }

    /**
     * 组件-日历
     *
     * @return
     */
    @GetMapping("/jsx3")
    public String jsx3() {
        return prefix + "/zujian/jsx3";
    }

    /**
     * 组件-navbar/仿今日头条导航
     *
     * @return
     */
    @GetMapping("/js4")
    public String js4() {
        return prefix + "/zujian/js4";
    }

    /**
     * 组件-tabbar
     *
     * @return
     */
    @GetMapping("/js5")
    public String js5() {
        return prefix + "/zujian/js5";
    }

    /**
     * 组件-轮播/swipe
     *
     * @return
     */
    @GetMapping("/js6")
    public String js6() {
        return prefix + "/zujian/js6";
    }

    /**
     * 组件-DPlayer视频播放器
     *
     * @return
     */
    @GetMapping("/dp/index")
    public String dpIndex() {
        return prefix + "/zujian/dp/index";
    }

    /**
     * 组件-Aplayer音乐播放器
     *
     * @return
     */
    @GetMapping("/dp/music")
    public String dpMusic() {
        return prefix + "/zujian/dp/music";
    }

    /**
     * 组件-音频播放
     *
     * @return
     */
    @GetMapping("/js7")
    public String js7() {
        return prefix + "/zujian/js7";
    }

    /**
     * 组件-js模板
     *
     * @return
     */
    @GetMapping("/js8")
    public String js8() {
        return prefix + "/zujian/js8";
    }

    /**
     * 组件-分页/点击加载更多
     *
     * @return
     */
    @GetMapping("/js9")
    public String js9() {
        return prefix + "/zujian/js9";
    }

    /**
     * 组件-底部滚动加载1
     *
     * @return
     */
    @GetMapping("/js10")
    public String js10() {
        return prefix + "/zujian/js10";
    }

    /**
     * 组件-底部滚动加载2
     *
     * @return
     */
    @GetMapping("/js15")
    public String js15() {
        return prefix + "/zujian/js15";
    }

    /**
     * 组件-下拉刷新
     *
     * @return
     */
    @GetMapping("/js14")
    public String js14() {
        return prefix + "/zujian/js14";
    }

    /**
     * 组件-ajax分页/分页样式
     *
     * @return
     */
    @GetMapping("/js101")
    public String js101() {
        return prefix + "/zujian/js101";
    }

    /**
     * 组件-新闻列表仿今日头条
     *
     * @return
     */
    @GetMapping("/js11")
    public String js11() {
        return prefix + "/zujian/js11";
    }

    /**
     * 组件-留言列表显示
     *
     * @return
     */
    @GetMapping("/js12")
    public String js12() {
        return prefix + "/zujian/js12";
    }

    /**
     * 组件-微信文章模板
     *
     * @return
     */
    @GetMapping("/js13")
    public String js13() {
        return prefix + "/zujian/js13";
    }

    /**
     * 组件-省简称弹出层
     *
     * @return
     */
    @GetMapping("/js16")
    public String js16() {
        return prefix + "/zujian/js16";
    }

    /**
     * 组件-JSSDK演示1.4
     *
     * @return
     */
    @GetMapping("/js17")
    public String js17() {
        return prefix + "/zujian/js17";
    }

    /**
     * 组件-侧边栏
     *
     * @return
     */
    @GetMapping("/js18")
    public String js18() {
        return prefix + "/zujian/js18";
    }

    /**
     * js插件-js自定义方法
     *
     * @return
     */
    @GetMapping("/p1")
    public String p1() {
        return prefix + "/js/p1";
    }

    /**
     * js插件-fullpage
     *
     * @return
     */
    @GetMapping("/p2")
    public String p2() {
        return prefix + "/js/p2";
    }

    /**
     * js插件-摇一摇
     *
     * @return
     */
    @GetMapping("/p3")
    public String p3() {
        return prefix + "/js/p3";
    }

    /**
     * js插件-图片懒加载
     *
     * @return
     */
    @GetMapping("/p4")
    public String p4() {
        return prefix + "/js/p4";
    }

    /**
     * js插件-微信分享/关注
     *
     * @return
     */
    @GetMapping("/p5")
    public String p5() {
        return prefix + "/js/p5";
    }

    /**
     * js插件-二维码/名片生成
     *
     * @return
     */
    @GetMapping("/p6")
    public String p6() {
        return prefix + "/js/p6";
    }

    /**
     * js插件-axios
     *
     * @return
     */
    @GetMapping("/p7")
    public String p7() {
        return prefix + "/js/p7";
    }

    /**
     * js插件-f2移动图表
     *
     * @return
     */
    @GetMapping("/p8")
    public String p8() {
        return prefix + "/js/p8";
    }

    /**
     * js插件-vconsole
     *
     * @return
     */
    @GetMapping("/p9")
    public String p9() {
        return prefix + "/js/p9";
    }

    /**
     * js插件-mock模拟json数据
     *
     * @return
     */
    @GetMapping("/p10")
    public String p10() {
        return prefix + "/js/p10";
    }

    /**
     * js插件-帮助
     *
     * @return
     */
    @GetMapping("/help1")
    public String help1() {
        return prefix + "/help1";
    }

}
