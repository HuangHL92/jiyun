package com.ruoyi.framework.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.ruoyi.common.enums.UserStatus;
import org.apache.ibatis.reflection.MetaObject;


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

        String currentUser ="";
        Date date = new Date();
        // 创建者
        Object fieldValue = getFieldValByName(COMMON_FIELD_CREATE_BY, metaObject);
        if (fieldValue == null)
        {
            setFieldValByName(COMMON_FIELD_CREATE_BY, currentUser, metaObject);
        }
        // 更新者
        fieldValue = getFieldValByName(COMMON_FIELD_UPDATE_BY, metaObject);
        if (fieldValue == null )
        {
            setFieldValByName(COMMON_FIELD_UPDATE_BY, currentUser, metaObject);
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
        String currentUser ="";

        Date date = new Date();
        // 更新者
        Object fieldValue = getFieldValByName(COMMON_FIELD_UPDATE_BY, metaObject);
        if (fieldValue == null && currentUser != null)
        {
            setFieldValByName(COMMON_FIELD_UPDATE_BY, currentUser, metaObject);
        }
        // 更新时间：必须要更新
        setFieldValByName(COMMON_FIELD_UPDATE_TIME, date, metaObject);

    }
}
