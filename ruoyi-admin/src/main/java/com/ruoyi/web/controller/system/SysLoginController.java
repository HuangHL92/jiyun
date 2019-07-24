package com.ruoyi.web.controller.system;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.framework.shiro.service.SysPasswordService;
import com.ruoyi.framework.shiro.web.filter.captcha.CaptchaValidateFilter;
import com.ruoyi.framework.util.JsonFileUtils;
import com.ruoyi.framework.util.ShiroUtils;
import com.ruoyi.system.domain.SysUser;
import com.ruoyi.system.service.ISysUserService;
import com.ruoyi.web.websocket.SocketServer;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.ruoyi.common.base.AjaxResult;
import com.ruoyi.common.utils.ServletUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.framework.web.base.BaseController;

/**
 * 登录验证
 * 
 * @author ruoyi
 */
@Controller
public class SysLoginController extends BaseController
{

    @Autowired
    private ISysUserService userService;

    @Autowired
    private CaptchaValidateFilter captchaValidateFilter;

    @Autowired
    private SysPasswordService passwordService;

    @GetMapping("/login")
    public String login(HttpServletRequest request, HttpServletResponse response)
    {
        // 如果是Ajax请求，返回Json字符串。
        if (ServletUtils.isAjaxRequest(request))
        {
            return ServletUtils.renderString(response, "{\"code\":\"1\",\"msg\":\"未登录或登录超时。请重新登录\"}");
        }

        return "login";
    }

    @PostMapping("/login")
    @ResponseBody
    public AjaxResult ajaxLogin(String username, String password, Boolean rememberMe)
    {
        UsernamePasswordToken token = new UsernamePasswordToken(username, password, rememberMe);
        Subject subject = SecurityUtils.getSubject();
        try
        {
            subject.login(token);

            //socket消息通知
            SysUser user = userService.selectUserByLoginName(username);
            String msg = user.getUserName() + "，上线啦！";
            SocketServer.sendMessage(msg, "onlineNotice");

            return success();
        }
        catch (AuthenticationException e)
        {
            String msg = "用户或密码错误";
            if (StringUtils.isNotEmpty(e.getMessage()))
            {
                msg = e.getMessage();
            }
            return error(msg);
        }
    }

    @GetMapping("/unauth")
    public String unauth()
    {
        return "/error/unauth";
    }



    /**
     * 用户注册
     */
    @GetMapping("/register")
    public String register(ModelMap mmap)
    {
        return "register";
    }


    /**
     * 用户注册(提交)
     */
    @Log(title = "用户注册", businessType = BusinessType.INSERT)
    @PostMapping("/register")
    @ResponseBody
    public AjaxResult register(HttpServletRequest request, SysUser user, ModelMap mmap)
    {
        String vcode = request.getParameter("validateCode");
        if(StringUtils.isEmpty(user.getLoginName()) ||
                StringUtils.isEmpty(user.getPassword())) {
             return  error("请完善信息!");
        }

        if(user.getPassword().length()<6) {
            return  error("密码不能少于6位");
        }

        //1.验证码验证
        if(!captchaValidateFilter.validateResponse(request,vcode)) {
            return  error("验证码错误!");
        }

        //2.验证用户唯一
        String hasUser= userService.checkLoginNameUnique(user.getLoginName());
        if("1".equals(hasUser)) {
            return  error("用户名已经存在!");
        }

        //3.验证手机号码唯一
        String hasPhone= userService.checkPhoneUnique(user);
        if("1".equals(hasPhone)) {
            return  error("手机号码已经存在!");
        }
        //4.用户保存
        user.setSalt(ShiroUtils.randomSalt());
        user.setPassword(passwordService.encryptPassword(user.getLoginName(), user.getPassword(), user.getSalt()));
        user.setCreateBy(ShiroUtils.getLoginName());
        userService.insertUser(user);
        // 删除组织结构json文件
        JsonFileUtils.deleteOrgJsonFile();
        return success();
    }



}
