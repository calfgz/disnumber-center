/*
 * Copyright 2023-2024 the leader
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

create table rule_instance
(
    id          int(11) unsigned auto_increment comment '编号'
        primary key,
    code        varchar(50)                          not null comment '实例key',
    description varchar(1000)                        null comment '实例描述',
    is_active   tinyint(1) default 1                 not null comment '是否启用',
    constraint idx_key
        unique (code)
)
    comment '规则实例';

create table rule
(
    id          int(11) unsigned auto_increment comment '编号'
        primary key,
    type        CHAR(7)                                    not null comment '规则类型(1,常量；2，日期；3，枚举；4，流水号；5，部门；6，指定值)',
    no          int(11) unsigned                           not null comment '序号',
    code        varchar(50)                                null comment '枚举为判定key |  部门为操作人key | 指定值key',
    val         varchar(1000)                              null comment '常量值  | 枚举值（json格式）',
    format      varchar(50)                                null comment '日期格式',
    len         int                                        null comment '流水号长度',
    min         int                                        null comment '最小值',
    max         int                                        null comment '最大值',
    step        int                                        null comment '步长',
    padding     varchar(1)                                 null comment '不足 补充定值',
    instance_id int(11) unsigned                           not null comment '实例编号',
    is_active   tinyint(1)       default 1                 not null comment '是否启用',
    constraint fgk_rule_to_instance
        foreign key (instance_id) references rule_instance (id)
)
    comment '规则表';

create table rule_instance_history
(
    id            bigint unsigned auto_increment
        primary key,
    instance_code varchar(50)                         not null comment '实例编码',
    code          varchar(255)                        not null comment '编码',
    val           int                                 not null comment '流水值',
    constraint fgk_history_to_instance
        foreign key (instance_code) references rule_instance (code)
)
    comment '规则实例记录表';

create index idx_instance_code
    on rule_instance_history (instance_code);

create index idx_instance_code_and_code
    on rule_instance_history (instance_code, code);


