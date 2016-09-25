#数据库创建脚本
#创建数据库
create database seckill;
#--使用数据库
use seckill;
#-- 创建秒杀表
create table seckill(
  seckill_id bigint not null AUTO_INCREMENT COMMENT '商品库存id',
  name varchar(120) not null COMMENT '商品名称',
  number int not null COMMENT '库存数量',
  start_time timestamp not null COMMENT '秒杀开启时间',
  end_time timestamp not null COMMENT '秒杀结束时间',
  create_time timestamp not null DEFAULT current_timestamp COMMENT '创建时间',
  primary KEY (seckill_id),
  key idx_start_time(start_time),
  key idx_end_time(end_time),
  key idx_create_time(create_time)
)ENGINE=InnoDB AUTO_INCREMENT=1000 DEFAULT CHARSET=utf8 COMMENT='秒杀表';

#---初始化数据
insert into seckill(name, number, start_time, end_time)
 VALUES ('1000元秒杀iphone6', 100, '2016-06-29 00:00:00', '2016-06-30 00:00:00'),
        ('100元秒杀ipd2', 100, '2016-06-29 00:00:00', '2016-06-30 00:00:00'),
('500元秒杀笔记本', 100, '2016-06-29 00:00:00', '2016-06-30 00:00:00');

#秒杀成功明细表，用户登录的认证相关信息
create table success_killed(
  seckill_id bigint NOT NULL comment '秒杀商品id',
  user_phone bigint NOT NULL comment '用户手机号',
  state TINYINT NOT NULL  DEFAULT -1 comment '状态标识：-1：无效 0：成功 1：已付款 2：已发货',
  create_time timestamp not null comment '创建时间',
  PRIMARY KEY (seckill_id, user_phone),/* 联合主键 */
  key idx_create_time(create_time)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='秒杀成功明细表';

--为什么手写DDL
--记录每次上线的DDL修改
--上线v1.1
ALTER TABLE seckill
    DROP index idx_create_time ,
add index idx_c_s(start_time, create_time);
