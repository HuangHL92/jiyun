package com.ruoyi.common.config;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.YamlUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 全局配置类
 *
 * @author ruoyi
 */
@Component
public class Global {
    private static final Logger log = LoggerFactory.getLogger(Global.class);

    private static String profile;

    @Value("${spring.profiles.active:dev}")
    public void setProfile(String param) {
        this.profile= param;
    }

    private static String NAME = "application.yml";

    /**
     * 当前对象实例
     */
    private static Global global = null;

    /**
     * 保存全局属性值
     */
    private static Map<String, String> map = new HashMap<String, String>();

    private Global() {
    }

    /**
     * 静态工厂方法 获取当前对象实例 多线程安全单例模式(使用双重同步锁)
     */

    public static synchronized Global getInstance() {
        if (global == null) {
            synchronized (Global.class) {
                if (global == null)
                    global = new Global();
            }
        }
        return global;
    }

    /**
     * 获取配置
     */
    public static String getConfig(String key) {
        String value = map.get(key);
        if (value == null) {
            Map<?, ?> yamlMap = null;
            try {
                yamlMap = YamlUtil.loadYaml(NAME);
                value = String.valueOf(YamlUtil.getProperty(yamlMap, key));
                if(StringUtils.isEmpty(value) || "null".equals(value)) {
                    yamlMap = YamlUtil.loadYaml("application-" + profile + ".yml");
                    value = String.valueOf(YamlUtil.getProperty(yamlMap, key));
                }
                map.put(key, value != null ? value : StringUtils.EMPTY);
            } catch (FileNotFoundException e) {
                log.error("获取全局配置异常 {}", key);
            }
        }
        return value;
    }

    /**
     * 获取项目名称
     */
    public static String getName() {
        return StringUtils.nvl(getConfig("ruoyi.name"), "RuoYi");
    }

    /**
     * 获取项目版本
     */
    public static String getVersion() {
        return StringUtils.nvl(getConfig("ruoyi.version"), "1.1.0");
    }

    /**
     * 获取版权年份
     */
    public static String getCopyrightYear() {
        return StringUtils.nvl(getConfig("ruoyi.copyrightYear"), "2019");
    }

    /**
     * 获取ip地址开关
     */
    public static Boolean isAddressEnabled() {
        return Boolean.valueOf(getConfig("ruoyi.addressEnabled"));
    }

    /**
     * 获取文件上传路径
     */
    public static String getProfile() {
        return getConfig("ruoyi.profile");
    }

    /**
     * 获取头像上传路径
     */
    public static String getAvatarPath() {
        return getConfig("ruoyi.profile") + "avatar/";
    }

    /**
     * 获取下载路径
     */
    public static String getDownloadPath() {
        return getConfig("ruoyi.profile") + "download/";
    }

    /**
     * 获取上传路径
     */
    public static String getUploadPath() {
        return getConfig("ruoyi.profile") + "upload/";
    }

    /**
     * 获取Json文件路径
     */
    public static String getJsonPath() {
        return getConfig("ruoyi.profile") + "json/";
    }

    /**
     * dataX Json 文件路径
     *
     * @return
     */
    public static String getJsonDataXPath()
    {
        return getJsonPath() + "dataXJson/";
    }

    /**
     * dataX log日志文件
     *
     * @return
     */
    public static String getJsonDataXLogPath()
    {
        return getJsonPath() + "dataXLog/";
    }
    public static String getDataXExePath(){
        return getConfig("ruoyi.dataX");
    }
    /**
     * 获取作者
     */
    public static String getAuthor() {
        return StringUtils.nvl(getConfig("gen.author"), "ruoyi");
    }

    /**
     * 生成包路径
     */
    public static String getPackageName() {
        return StringUtils.nvl(getConfig("gen.packageName"), "com.ruoyi.project.module");
    }

    /**
     * 是否自动去除表前缀
     */
    public static String getAutoRemovePre() {
        return StringUtils.nvl(getConfig("gen.autoRemovePre"), "true");
    }

    /**
     * 表前缀(类名不会包含表前缀)
     */
    public static String getTablePrefix() {
        return StringUtils.nvl(getConfig("gen.tablePrefix"), "sys_");
    }


    /**
     * 获取websocketServer地址
     */
    public static String getWebSocketAddress() {
        return StringUtils.nvl(getConfig("jiyun.websocket.url"), "");
    }

    /**
     * 获取用户密码校验正则
     */
    public static String getPasswordRegex() {
        return StringUtils.nvl(getConfig("user.password.regex"), "");
    }

    /**
     * 获取用户密码校验正则错误消息
     */
    public static String getPasswordMessage() {
        return StringUtils.nvl(getConfig("user.password.message"), "");
    }
}
