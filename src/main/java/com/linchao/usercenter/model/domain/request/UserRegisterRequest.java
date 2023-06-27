package com.linchao.usercenter.model.domain.request;

import lombok.Data;

import java.io.Serializable;

/**
 *  用户注册请求体
 *
 * @author Steven
 * @create 2023-06-26-9:24
 */
@Data
public class UserRegisterRequest implements Serializable {

    private static final long serialVersionUID = -7532772306750033515L;

    private String userAccount;

    private String userPassword;

    private String checkPassword;

    private String planetCode;
}
