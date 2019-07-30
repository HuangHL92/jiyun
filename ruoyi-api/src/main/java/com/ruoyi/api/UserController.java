package com.ruoyi.api;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;
import cn.hutool.json.JSONObject;
import com.ruoyi.area.auth.domain.AuthClientDetails;
import com.ruoyi.base.ApiBaseController;
import com.ruoyi.common.AuthConstants;
import com.ruoyi.system.domain.SysUser;
import com.ruoyi.system.service.ISysUserService;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.codec.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户相关controller
 *
 * @author tao.liang
 * @date 2019/7/24
 */
@Controller
public class UserController extends ApiBaseController {

    @Autowired
    private ISysUserService userService;

    /**
     * 登录页面
     * @param request
     * @return
     */
    @GetMapping("/login")
    public ModelAndView loginPage(HttpServletRequest request) {
        String redirectUrl = request.getParameter("redirectUri");
        if (StringUtils.isNoneBlank(redirectUrl)) {
            HttpSession session = request.getSession();
            //将回调地址添加到session中
            session.setAttribute(AuthConstants.SESSION_LOGIN_REDIRECT_URL, redirectUrl);
        }

        return new ModelAndView("login");
    }

    /**
     * 登录验证
     * @param request
     * @return
     */
    @PostMapping("/check")
    @ResponseBody
    public Map<String, Object> check(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> result = new HashMap<>(2);

        //用户名
        String username = request.getParameter("username");
        //密码
        String password = request.getParameter("password");
        //密码
        Boolean rememberMe = Boolean.valueOf(request.getParameter("rememberMe"));

        if (StringUtils.isNoneBlank(username) && StringUtils.isNoneBlank(password)) {
            //1. 登录验证
            Map<String, Object> checkMap = userService.checkLogin(username, password);
            Boolean loginResult = (Boolean) checkMap.get("result");
            SysUser correctUser = (SysUser) checkMap.get("user");

            //登录验证通过
            if (loginResult != null && loginResult) {
                //2. session中添加用户信息
                HttpSession session = request.getSession();
                session.setAttribute(AuthConstants.SESSION_USER, correctUser);

                //3. 是否记住我
                if (rememberMe) {
                    // 写Cookie的套路：先new一个cookie，然后调用response的addCookie方法就可以写cookie了
                    AES aes = SecureUtil.aes(Base64.decode(AuthConstants.AEC_KEY));
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("username", "admin");
                    jsonObject.put("password", "1q2w3e4r");
                    System.out.println(jsonObject.toString());
                    String encryptHex = aes.encryptHex(jsonObject.toString());
                    Cookie rememberMeCookie = new Cookie("rememberMe", encryptHex);
                    rememberMeCookie.setMaxAge(7 * 24 * 60 * 60); //过期时间为一周（备注：值为-1会话级cookie关闭浏览器失效 值为 0不记录cookie）
                    rememberMeCookie.setPath(request.getContextPath() + "/"); //设置cookie到当前工程的路径

                    response.addCookie(rememberMeCookie);
                }

                //4. 返回给页面的数据
                result.put("code", 200);
                //登录成功之后的回调地址
                String redirectUrl = (String) session.getAttribute(AuthConstants.SESSION_LOGIN_REDIRECT_URL);
                session.removeAttribute(AuthConstants.SESSION_LOGIN_REDIRECT_URL);

                if (StringUtils.isNoneBlank(redirectUrl)) {
                    result.put("redirect_uri", redirectUrl);
                }
            } else {
                if ("1".equals(correctUser.getStatus())) {
                    result.put("msg", "该用户已被管理员禁用！");
                } else {
                    result.put("msg", "用户名或密码错误！");
                }
            }
        } else {
            result.put("msg", "请求参数不能为空！");
        }

        return result;
    }

    /**
     * 注销
     *
     * @return org.springframework.web.servlet.ModelAndView
     * @author zifangsky
     * @date 2018/8/3 11:47
     * @since 1.0.0
     */
    @GetMapping("/logout")
    public ModelAndView logout(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        session.removeAttribute(AuthConstants.SESSION_USER);
        // 清空cookie
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("rememberMe".equals(cookie.getName())) {
                    Cookie rememberMeCookie = new Cookie(cookie.getName(), null);
                    rememberMeCookie.setMaxAge(0);
                    response.addCookie(rememberMeCookie);
                    break;
                }
            }
        }
        return new ModelAndView("redirect:/login");
    }

    /**
     * 用户首页
     *
     * @return java.lang.String
     * @author zifangsky
     * @date 2018/8/3 11:13
     * @since 1.0.0
     */
    @GetMapping("/user/userIndex")
    public String userIndex(HttpServletRequest request, ModelMap modelMap) {
        HttpSession session = request.getSession();
        // TODO 查询当前用户可以访问的系统
        List<AuthClientDetails> clientList = new ArrayList<>();
        AuthClientDetails client1 = new AuthClientDetails();
        client1.setClientId("x3qwrgrO1wYdz72joZ8YyIuD");
        client1.setClientName("Ruoyi管理后台");
        clientList.add(client1);
        modelMap.put("clientList", clientList);

        // 返回当前用户
        modelMap.put("currentUser", session.getAttribute(AuthConstants.SESSION_USER));
        return "userIndex";
    }
}
