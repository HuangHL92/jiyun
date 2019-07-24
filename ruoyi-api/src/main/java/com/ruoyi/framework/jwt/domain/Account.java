package com.ruoyi.framework.jwt.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description $功能描述$
 * @Author yufei
 * @Date 2019-03-08 10:40
 **/

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Account {
    String Id;
    String username;
    String password;
}
