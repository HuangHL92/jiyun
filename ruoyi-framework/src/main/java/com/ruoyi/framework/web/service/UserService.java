package com.ruoyi.framework.web.service;

import com.ruoyi.system.domain.SysUser;
import com.ruoyi.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * RuoYi首创 html调用 thymeleaf 实现参数管理
 * 
 * @author ruoyi
 */
@Service("user")
public class UserService
{
    @Autowired
    private ISysUserService userService;


    public String getName(String id)
    {
        SysUser user= userService.selectUserById(id);
        return user==null?"":user.getUserName();
    }

    public List<SysUser>  getList()
    {
        List<SysUser> list = userService.selectUserList(new SysUser());
        return list;
    }
}
