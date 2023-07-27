-- auto-generated definition
CREATE TABLE `user` (
                        `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
                        `username` varchar(256) DEFAULT NULL COMMENT '用户昵称',
                        `userAccount` varchar(256) DEFAULT NULL COMMENT '账号\r\n',
                        `avatarUrl` varchar(1024) DEFAULT NULL COMMENT '用户头像',
                        `gender` tinyint(4) DEFAULT NULL COMMENT '性别',
                        `userPassword` varchar(512) NOT NULL COMMENT '密码',
                        `phone` varchar(128) DEFAULT NULL COMMENT '电话\r\n',
                        `userStatus` int(11) NOT NULL DEFAULT '0' COMMENT '状态 0	正常',
                        `email` varchar(512) DEFAULT NULL COMMENT '邮箱',
                        `createTime` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                        `updateTime` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                        `isDelete` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否删除',
                        `userRole` int(11) NOT NULL DEFAULT '0' COMMENT '用户角色 0 - 普通用户 1 - 管理员\n',
                        `planetCode` varchar(512) DEFAULT NULL COMMENT '星球编号',
                        `tags` varchar(1024) DEFAULT NULL COMMENT '标签 json 列表',
                        PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户'



alter table user add COLUMN tags varchar(1024) null comment '标签列表';


-- auto-generated definition
create table tag
(
    id         bigint auto_increment comment 'id'
        primary key,
    tagName    varchar(256)                       null comment '标签名称',
    userId     varchar(256)                       null comment '用户 id
',
    parentId   varchar(256)                       null comment '父标签 id',
    isParent   tinyint                            null comment '0 - 不是, 1 - 父标签',
    createTime datetime default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete   tinyint  default 0                 not null comment '是否删除',
    constraint uniIdx_tagName
        unique (tagName)
)
    comment '标签';

create index idx_userId
    on tag (userId);

