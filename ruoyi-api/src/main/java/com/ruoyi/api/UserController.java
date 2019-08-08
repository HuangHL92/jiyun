package com.ruoyi.api;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;
import cn.hutool.json.JSONObject;
import com.ruoyi.area.auth.domain.AuthClientDetails;
import com.ruoyi.base.ApiBaseController;
import com.ruoyi.common.AuthConstants;
import com.ruoyi.common.base.ApiResult;
import com.ruoyi.common.enums.QrCodeEnmu;
import com.ruoyi.common.enums.ResponseCode;
import com.ruoyi.framework.redis.RedisService;
import com.ruoyi.service.PasswordService;
import com.ruoyi.system.domain.SysUser;
import com.ruoyi.system.service.ISysUserService;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.codec.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 用户相关controller
 *
 * @author tao.liang
 * @date 2019/7/24
 */
@ApiIgnore()
@Controller
public class UserController extends ApiBaseController {


    @Autowired
    private RedisService redisService;
    @Autowired
    private ISysUserService userService;
    @Autowired
    private PasswordService passwordService;

    /**
     * 首页
     *
     * @param request
     * @return
     */
    @GetMapping(value = {"/", "/index"})
    public ModelAndView index(HttpServletRequest request, ModelMap modelMap) {
        // session共享测试
        HttpSession session = request.getSession();
        //将回调地址添加到session中
        modelMap.put("sessionId", session.getId());

        return new ModelAndView("index");
    }

    /**
     * 登录页面
     *
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
     * 登陆首页获取扫一扫登陆的二维码内容
     * 1、二维码内容的有效时间是5分钟，生成的code是唯一码，存在redis缓存中，value里面带有该code的登陆状态
     *
     * @param oldContext
     * @return
     */
    @PostMapping(value = "/getQrcodeContent")
    @ResponseBody
    public ApiResult getQrcodeContent(@RequestParam("context") String oldContext) {
        //如果页面有旧的二维码，同时请求新的二维码内容，则直接删除旧内容
        if (StrUtil.isNotEmpty(oldContext)) {
            if (redisService.exists(AuthConstants.QRCODE_LOGIN + oldContext.replace(AuthConstants.QRCODE_HEADER, ""))) {
                redisService.del(AuthConstants.QRCODE_LOGIN + oldContext.replace(AuthConstants.QRCODE_HEADER, ""));
            }
        }

        String code = cn.hutool.core.codec.Base64.encode(UUID.randomUUID().toString());
        String context = AuthConstants.QRCODE_HEADER + code;
        //将生成的code存入redis，失效时间为120S
        redisService.setWithExpire(AuthConstants.QRCODE_LOGIN + code, QrCodeEnmu.logout.toString(), 120, TimeUnit.SECONDS);
        return success(context);
    }

    /**
     * web端与服务器建立连接检查当前用户是否有做登陆动作
     *
     * @param context
     * @param type
     * @return
     */
    @PostMapping(value = "/qrcodeCheckLogin")
    @ResponseBody
    public ApiResult qrcodeCheckLogin(@RequestParam("context") String context,
                                      @RequestParam("type") String type,
                                      HttpServletRequest request) throws Exception {
        //参数判断
        if (StrUtil.isEmpty(context)) {
            return error(ResponseCode.INVALID_QRCODE);
        }

        //统一一个开始时间，每次请求超过10s时自动跳出循环结束
        long startTime = System.currentTimeMillis();
        String code = context.replace(AuthConstants.QRCODE_HEADER, "");
        ApiResult result;
        while (true) {
            Thread.sleep(500);
            //logger.info("retry check login...");
            //检查redis是否中还存在二维码内容
            if (!redisService.exists(AuthConstants.QRCODE_LOGIN + code)) {
                return error(ResponseCode.INVALID_QRCODE);
            } else {
                String status = redisService.getObj(AuthConstants.QRCODE_LOGIN + code);
                //如果status 的值是 scan，则表示该code已经被手机扫描，返回页面提示在手机上确认登陆
                //如果status 的值是 login，则表示该code处于登录状态，则返回前端状态信息
                //如果status 的值是 cancel，则表示该code为取消登录
                //如果status 的值是 logout，则表示该code尚未被扫描
                if (QrCodeEnmu.scan.toString().equals(status)) {
                    //如果传入的type的状态值不为空，则表明已经扫描成功，在等待确认下一步操作
                    if (QrCodeEnmu.scan.toString().equals(type)) {
                        long endTime = System.currentTimeMillis();
                        long exeTime = endTime - startTime;
                        //请求大于10s，则跳出循环结束
                        if (exeTime >= 10000) {
                            result = error(ResponseCode.REQUEST_TIME_OUT);
                            break;
                        }
                    } else {
                        return success(ResponseCode.SCAN_SUCCESS);
                    }
                } else if (QrCodeEnmu.cancel.toString().equals(status)) {
                    redisService.del(AuthConstants.QRCODE_LOGIN + code); //删除redis中该二维码的缓存信息
                    return success(ResponseCode.CANCEL_SUCCESS);
                } else if (status.startsWith("login_")) {
                    redisService.del(AuthConstants.QRCODE_LOGIN + code);
                    String userCode = status.replace("login_", "");

                    SysUser sysUser = userService.selectUserByLoginName(userCode);

                    // 登录操作
                    // session中添加用户信息
                    HttpSession session = request.getSession();
                    session.setAttribute(AuthConstants.SESSION_USER, sysUser);

                    return success();
                } else {
                    long endTime = System.currentTimeMillis();
                    long exeTime = endTime - startTime;
                    //请求大于10s，则跳出循环结束
                    if (exeTime >= 10000) {
                        result = error(ResponseCode.REQUEST_TIME_OUT);
                        break;
                    }
                }
            }
        }
        return result;
    }

    /**
     * 登录验证
     *
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
            Map<String, Object> checkMap = passwordService.checkLogin(username, password);
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
                    jsonObject.put("username", username);
                    jsonObject.put("password", password);
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
                if (correctUser != null && "1".equals(correctUser.getStatus())) {
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
     * @param request
     * @param response
     * @return
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
     * @param request
     * @param modelMap
     * @return
     */
    @GetMapping("/user/userIndex")
    public String userIndex(HttpServletRequest request, ModelMap modelMap) {
        HttpSession session = request.getSession();
        // TODO 查询当前用户可以访问的系统
        List<AuthClientDetails> clientList = new ArrayList<>();
        AuthClientDetails client1 = new AuthClientDetails();
        client1.setClientId("7Ugj6XWmTDpyYp8M8njG3hqx");
        client1.setClientName("本地测试系统");
        clientList.add(client1);
        modelMap.put("clientList", clientList);

        // 返回当前用户
        modelMap.put("currentUser", session.getAttribute(AuthConstants.SESSION_USER));
        return "userIndex";
    }

    /**
     * 模拟APP登录
     *
     * @param request
     * @param modelMap
     * @return
     */
    @GetMapping("/confirm")
    public String confirm(HttpServletRequest request, ModelMap modelMap) {

        return "confirm";
    }
}
