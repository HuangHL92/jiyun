package com.ruoyi.api;

import com.ruoyi.area.auth.domain.AuthClientDetails;
import com.ruoyi.area.auth.service.IAuthClientDetailsService;
import com.ruoyi.base.ApiBaseController;
import com.ruoyi.common.AuthConstants;
import com.ruoyi.service.AuthorizationService;
import com.ruoyi.system.domain.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * 基于oauth2.0相关的授权相关操作
 *  主动跳转到系统
 * @author tao.liang
 * @date 2019/7/24
 */
@ApiIgnore
@Controller
public class OauthDirectController extends ApiBaseController {

    @Autowired
    private AuthorizationService authorizationService;
    @Autowired
    private IAuthClientDetailsService authClientDetailsService;

    /**
     * 主动跳转到系统
     * @param request
     * @return
     */
    @ApiIgnore
    @PostMapping("/auth_direct")
    @ResponseBody
    public Map<String, Object> authDirect(HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>(2);

        HttpSession session = request.getSession();
        SysUser user = (SysUser) session.getAttribute(AuthConstants.SESSION_USER);

        // 客户端ID
        String clientId = request.getParameter("client_id");
        AuthClientDetails authClientDetails = authClientDetailsService.selectByClientId(clientId);

        // TODO 需要判断当前用户是否有该系统的访问权限

        // 回调url
        String redirectUri = authClientDetails.getRedirectUri();

        //生成Authorization Code
        String authorizationCode = authorizationService.createAuthorizationCode(clientId, user);

        result.put("code", 200);
        result.put("redirect_uri", redirectUri + "?code=" + authorizationCode);

        return result;
    }
}
