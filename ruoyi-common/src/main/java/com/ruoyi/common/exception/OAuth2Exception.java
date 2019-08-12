package com.ruoyi.common.exception;

import lombok.Data;

/**
 * 业务异常
 * 
 * @author ruoyi
 */
@Data
public class OAuth2Exception extends RuntimeException
{
    private static final long serialVersionUID = 1L;

    protected final int code;
    protected final String message;

}
