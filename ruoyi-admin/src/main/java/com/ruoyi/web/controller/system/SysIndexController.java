package com.ruoyi.web.controller.system;

import java.util.List;
import java.util.stream.Collectors;

import com.ruoyi.framework.util.CacheUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import com.ruoyi.common.config.Global;
import com.ruoyi.system.domain.SysMenu;
import com.ruoyi.system.domain.SysUser;
import com.ruoyi.system.service.ISysMenuService;
import com.ruoyi.framework.web.base.BaseController;

/**
 * 首页 业务处理
 * 
 * @author ruoyi
 */
@Controller
public class SysIndexController extends BaseController
{
    @Autowired
    private ISysMenuService menuService;

    @Autowired
    private CacheUtils cacheUtils;

    // 系统首页
    @GetMapping("/index")
    public String index(ModelMap mmap)
    {
        // 取身份信息
        SysUser user = getSysUser();

        // 根据用户id取出菜单
        List<SysMenu> menus = cacheUtils.getUserMenuCache().get(user.getLoginName());
        if(menus==null) {
            menus = menuService.selectMenusByUser(user);
            cacheUtils.getUserMenuCache().put(user.getLoginName(),menus);
        }

        //List<SysMenu> menus = menuService.selectMenusByUser(user);
        mmap.put("menus", menus);
        mmap.put("user", user);
        mmap.put("copyrightYear", Global.getCopyrightYear());
        return "index";
    }

    // 系统介绍
    @GetMapping("/system/main")
    public String main(ModelMap mmap)
    {
        mmap.put("version", Global.getVersion());
        return "main";
    }

    /***
     *
     * @param mmap
     * @param id
     * @return
     */
    @GetMapping("/system/menu_left")
    public String menu_left(ModelMap mmap,String id)
    {
        // 取身份信息
        SysUser user = getSysUser();

        // 根据用户id取出菜单
        List<SysMenu> menus = cacheUtils.getUserMenuCache().get(user.getLoginName());
        if(menus==null) {
            menus = menuService.selectMenusByUser(user);
            cacheUtils.getUserMenuCache().put(user.getLoginName(),menus);
        }
        //根据一级菜单ID进行过滤
        List<SysMenu> pm;
        pm = menus.stream().filter(u->u.getMenuId()==Long.parseLong(id)).collect(Collectors.toList());
        mmap.put("menus", pm.get(0));
        mmap.put("user", user);
        mmap.put("copyrightYear", Global.getCopyrightYear());
        return "menu_left";
    }


}
