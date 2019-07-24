package com.ruoyi.framework.web.base;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ruoyi.common.base.AjaxResult;
import com.ruoyi.common.page.PageDomain;
import com.ruoyi.common.page.TableDataInfo;
import com.ruoyi.common.page.TableSupport;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.framework.util.ShiroUtils;
import com.ruoyi.system.domain.SysUser;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

import java.beans.PropertyEditorSupport;
import java.util.Date;
import java.util.List;

/**
 * web层通用数据处理
 * 
 * @author ruoyi
 */
public class BaseController
{
    /***
     * 加密解密用KEY
     */
    //byte[] key = SecureUtil.generateKey(SymmetricAlgorithm.AES.getValue()).getEncoded();
    byte[] key = new byte[]{91,42,-39,-56,-13,79,-22,41,37,-61,-123,11,-20,56,107,57};

    /**
     * 将前台传递过来的数据预处理
     */
    @InitBinder
    public void initBinder(WebDataBinder binder)
    {
        // Date 类型转换
        binder.registerCustomEditor(Date.class, new PropertyEditorSupport()
        {
            @Override
            public void setAsText(String text)
            {
                setValue(DateUtils.parseDate(text));
            }
        });

        //int 类型转换  Add by yufei 2019-03-01 解决int型数据画面参数转递后台为“”时的问题
        binder.registerCustomEditor(int.class, new PropertyEditorSupport()
        {
            @Override
            public void setAsText(String text)
            {
                if("".equals(text)){
                    setValue(0);
                } else {
                    try {
                        setValue(Integer.parseInt(text));
                    }catch (Exception ex) {
                        setValue(0);
                    }
                }
            }
        });

    }

    /**
     * 设置请求分页数据
     */
    protected void startPage()
    {
        PageDomain pageDomain = TableSupport.buildPageRequest();
        Integer pageNum = pageDomain.getPageNum();
        Integer pageSize = pageDomain.getPageSize();
        if (StringUtils.isNotNull(pageNum) && StringUtils.isNotNull(pageSize))
        {
            String orderBy = pageDomain.getOrderBy();
            PageHelper.startPage(pageNum, pageSize, orderBy);
        }
    }

    /**
     * 响应请求分页数据
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected TableDataInfo getDataTable(List<?> list)
    {
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(0);
        rspData.setRows(list);
        rspData.setTotal(new PageInfo(list).getTotal());
        return rspData;
    }

    /**
     * 响应返回结果
     * 
     * @param rows 影响行数
     * @return 操作结果
     */
    protected AjaxResult toAjax(int rows)
    {
        return rows > 0 ? success() : error();
    }

    /**
     * 响应返回结果
     * 
     * @param result 结果
     * @return 操作结果
     */
    protected AjaxResult toAjax(boolean result)
    {
        return result ? success() : error();
    }

    /**
     * 返回成功
     */
    public AjaxResult success()
    {
        return AjaxResult.success();
    }

    /**
     * 返回失败消息
     */
    public AjaxResult error()
    {
        return AjaxResult.error();
    }

    /**
     * 返回成功消息
     */
    public AjaxResult success(String message)
    {
        return AjaxResult.success(message);
    }

    /**
     * 返回失败消息
     */
    public AjaxResult error(String message)
    {
        return AjaxResult.error(message);
    }

    /**
     * 返回错误码消息
     */
    public AjaxResult error(int code, String message)
    {
        return AjaxResult.error(code, message);
    }

    /**
     * 页面跳转
     */
    public String redirect(String url)
    {
        return StringUtils.format("redirect:{}", url);
    }

    public SysUser getSysUser()
    {
        return ShiroUtils.getSysUser();
    }

    public void setSysUser(SysUser user)
    {
        ShiroUtils.setSysUser(user);
    }

    public String getUserId()
    {
        return getSysUser().getUserId();
    }

    public String getLoginName()
    {
        return getSysUser().getLoginName();
    }


    /***
     * 主键加密
     * @param pk
     * @return
     */
    public String pk_encrypt(String pk) {

        // 构建
        AES aes = SecureUtil.aes(key);
        // 加密为16进制表示
        String encryptHex = aes.encryptHex(pk);

        return encryptHex;
    }

    /***
     * 主键解密
     * @param encryptpk
     * @return
     */
    public String pk_decrypt(String encryptpk)  {

        // 构建
        AES aes = SecureUtil.aes(key);
        // 解密为字符串
        String decryptStr = aes.decryptStr(encryptpk, CharsetUtil.CHARSET_UTF_8);

        return decryptStr;
    }


}
