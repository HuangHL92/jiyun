package com.ruoyi.common.base;

import java.util.Date;

/**
 * @Description $功能描述$
 * @Author yufei
 * @Date 2019-03-07 10:35
 **/
public class TokenEntity {

    private String token; //token

    private String userName; //用户名

    private Long expiresIn; //有效期  秒

    private Date createDate;  //创建时间

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }



}
