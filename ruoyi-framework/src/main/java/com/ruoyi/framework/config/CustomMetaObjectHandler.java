package com.ruoyi.framework.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.ruoyi.common.enums.UserStatus;
import com.ruoyi.framework.util.ShiroUtils;
import com.ruoyi.system.domain.SysUser;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.shiro.UnavailableSecurityManagerException;

import java.util.Date;

//import com.baomidou.mybatisplus.mapper.MetaObjectHandler;

public class CustomMetaObjectHandler implements MetaObjectHandler
{

    /** 创建者 */
    public static final String COMMON_FIELD_CREATE_BY = "createBy";
    /** 创建时间 */
    public static final String COMMON_FIELD_CREATE_TIME = "createTime";
    /** 更新者 */
    public static final String COMMON_FIELD_UPDATE_BY = "updateBy";
    /** 更新时间 */
    public static final String COMMON_FIELD_UPDATE_TIME = "updateTime";
    /** 逻辑删除标记 */
    public static final String COMMON_FIELD_DEL_FLAG = "delFlag";

    @Override
    public void insertFill(MetaObject metaObject)
    {
        SysUser currentUser = null;
        try {
            currentUser = ShiroUtils.getSysUser();
        } catch (UnavailableSecurityManagerException e) {

        }
        Date date = new Date();
        // 创建者
        Object fieldValue = getFieldValByName(COMMON_FIELD_CREATE_BY, metaObject);
        if (fieldValue == null && currentUser != null)
        {
            setFieldValByName(COMMON_FIELD_CREATE_BY, currentUser.getUserId(), metaObject);
        }
        // 更新者
        fieldValue = getFieldValByName(COMMON_FIELD_UPDATE_BY, metaObject);
        if (fieldValue == null && currentUser != null)
        {
            setFieldValByName(COMMON_FIELD_UPDATE_BY, currentUser.getUserId(), metaObject);
        }
        // 创建时间
        fieldValue = getFieldValByName(COMMON_FIELD_CREATE_TIME, metaObject);
        if (fieldValue == null)
        {
            setFieldValByName(COMMON_FIELD_CREATE_TIME, date, metaObject);
        }
        // 更新时间
        fieldValue = getFieldValByName(COMMON_FIELD_UPDATE_TIME, metaObject);
        if (fieldValue == null)
        {
            setFieldValByName(COMMON_FIELD_UPDATE_TIME, date, metaObject);
        }
        // 逻辑删除标记
        fieldValue = getFieldValByName(COMMON_FIELD_DEL_FLAG, metaObject);
        if (fieldValue == null)
        {
            setFieldValByName(COMMON_FIELD_DEL_FLAG, UserStatus.OK.getCode(), metaObject);
        }
    }

    @Override
    public void updateFill(MetaObject metaObject)
    {
        SysUser currentUser = null;
        try {
            currentUser = ShiroUtils.getSysUser();
        } catch (UnavailableSecurityManagerException e) {

        }
        Date date = new Date();
        // 更新者
        Object fieldValue = getFieldValByName(COMMON_FIELD_UPDATE_BY, metaObject);
        if (fieldValue == null && currentUser != null)
        {
            setFieldValByName(COMMON_FIELD_UPDATE_BY, currentUser.getUserId(), metaObject);
        }
        // 更新时间：必须要更新
//        fieldValue = getFieldValByName(COMMON_FIELD_UPDATE_TIME, metaObject);
//        if (fieldValue == null)
//        {
            setFieldValByName(COMMON_FIELD_UPDATE_TIME, date, metaObject);
//        }
    }
}
