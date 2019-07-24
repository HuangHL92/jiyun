//package com.ruoyi.api;
//
//import cn.hutool.core.util.RandomUtil;
//import cn.hutool.json.JSONObject;
//import cn.hutool.json.JSONUtil;
//import com.ruoyi.config.AuthGiteeConfig;
//import com.ruoyi.framework.shiro.service.SysPasswordService;
//import com.ruoyi.framework.util.ShiroUtils;
//import com.ruoyi.system.domain.SysUser;
//import com.ruoyi.system.service.ISysUserService;
//import com.ruoyi.web.websocket.SocketServer;
//import me.zhyd.oauth.config.AuthConfig;
//import me.zhyd.oauth.model.AuthResponse;
//import me.zhyd.oauth.request.AuthGiteeRequest;
//import me.zhyd.oauth.request.AuthRequest;
//import org.apache.shiro.SecurityUtils;
//import org.apache.shiro.authc.AuthenticationException;
//import org.apache.shiro.authc.UsernamePasswordToken;
//import org.apache.shiro.subject.Subject;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.ResponseBody;
//
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//
///**
// * gitee（码云）第三方登录
// *
// * @author tao.liang
// * @date 2019年3月26日
// */
//@Controller
//@RequestMapping("/oauth/gitee")
//public class GiteeAuthController {
//
//    // 默认密码
//    private static final String DEFAULT_PASSWORD = "123456";
//
//    @Autowired
//    private ISysUserService userService;
//    @Autowired
//    private SysPasswordService passwordService;
//    @Autowired
//    private AuthGiteeConfig config;
//
//    /**
//     * 访问url
//     *
//     * @param response
//     * @throws IOException
//     */
//    @RequestMapping("")
//    @ResponseBody
//    public void url(HttpServletResponse response) throws IOException {
//        // 如果已经登录直接返回首页
//        if (ShiroUtils.getSysUser() != null) {
//            response.sendRedirect("/index");
//            return;
//        }
//        AuthRequest authRequest = getGiteeAuthRequest();
//        // 生成授权页面
//        response.sendRedirect(authRequest.authorize());
//    }
//
//    /**
//     * 回调地址
//     *
//     * @param code
//     * @return
//     */
//    @RequestMapping("/login")
//    public String login(String code) {
//        AuthRequest authRequest = getGiteeAuthRequest();
//        // 1. 授权登录后会返回一个code，用这个code进行登录
//        AuthResponse response = authRequest.login(code);
//        // 2. 获取得到的信息：用户名、头像、昵称
//        JSONObject json = JSONUtil.parseObj(response.getData());
//        String username = json.getStr("username");
//        String avatar = json.getStr("avatar");
//        String nickname = json.getStr("nickname");
//
//        // 3. 根据username查询数据库用户，auth_gitee字段判断是否存在
//        SysUser user = userService.selectUserByAuthGitee(username);
//        // 4. 如果用户不存在创建用户
//        if (user == null) {
//            user = new SysUser();
//            // 用户名为8位随机数
//            user.setLoginName(RandomUtil.randomString(8));
//            user.setSalt(ShiroUtils.randomSalt());
//            user.setPassword(passwordService.encryptPassword(user.getLoginName(), DEFAULT_PASSWORD, user.getSalt()));
//            user.setUserName(nickname);
//            user.setAvatar(avatar);
//            user.setAuthGitee(username);
//            user.setCreateBy("gitee");
//            userService.insertUser(user);
//        }
//        // 5. 执行登录操作
//        UsernamePasswordToken token = new UsernamePasswordToken(user.getLoginName(), DEFAULT_PASSWORD, false);
//        Subject subject = SecurityUtils.getSubject();
//        try {
//            subject.login(token);
//
//            //socket消息通知
//            String msg = user.getUserName() + "，上线啦！";
//            SocketServer.sendMessage(msg, "onlineNotice");
//
//            return "redirect:/index";
//        } catch (AuthenticationException e) {
//            return "error";
//        }
//    }
//
//    /**
//     * 取消授权
//     *
//     * @param token
//     * @return
//     */
//    @RequestMapping("/revoke/{token}")
//    @ResponseBody
//    public Object revokeAuth(@PathVariable("token") String token) {
//        AuthRequest authRequest = getGiteeAuthRequest();
//        return authRequest.revoke(token);
//    }
//
//    /**
//     * 创建授权request
//     *
//     * @return
//     */
//    private AuthRequest getGiteeAuthRequest() {
//        return new AuthGiteeRequest(AuthConfig.builder()
//                .clientId(config.getClientId())
//                .clientSecret(config.getClientSecret())
//                .redirectUri(config.getRedirectUri())
//                .build());
//    }
//
//}