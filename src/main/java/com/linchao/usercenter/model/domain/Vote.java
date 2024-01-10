package com.linchao.usercenter.model.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户
 * @TableName vote
 */
@TableName(value ="vote")
@Data
public class Vote implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 状态 0	正常
     */
    private Integer userStatus;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否删除
     */
    @TableLogic
    private Integer isDelete;

    /**
     * 微信编号
     */
    private String wechatId;

    /**
     * 队伍1
     */
    private Integer teamOne;

    /**
     * 队伍2
     */
    private Integer teamTwo;

    /**
     * 队伍3
     */
    private Integer teamThree;

    /**
     * 队伍4
     */
    private Integer teamFour;

    /**
     * 队伍5
     */
    private Integer teamFive;

    /**
     * 队伍6
     */
    private Integer teamSix;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}