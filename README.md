## bitfree
bitfree 后端部分

## 本地启动需安装MySQL/JDK

### 如果要使用本地DB，请设置application.yml变量 datasource: config-path: ${本地db.json所在目录}

#### db.json格式如下：
## DB配置需要在本地datasource.config-path配置目录创建 db.json，内部格式
### 本地
{
"url": "jdbc:mysql://127.0.0.1:3306/bitfree?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai&allowMultiQueries=true",
"username": "xxx",
"password": "xxxx"
}
#### 本地创建DB执行resources下的DDL
bitfree_日期.sql

### 如果使用公共 remote test环境DB, 只需要把application.yml active 设置为dev即可加载application-dev.yml 即当前resources下的db.json
#### 测试环境DB登录账号（社区版）
邮箱：test@126.com
密码：test123456

### @Rin 提供了公共 remote dev地址
{
"url": "jdbc:mysql://rm-wz99hc7z0q2izxu5jto.mysql.rds.aliyuncs.com:3306/bitfree_dev?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai&allowMultiQueries=true",
"username": "bitfree",
"password": "123456"
}
收件邮箱: bitfree_dev@outlook.com 密码: dev2Bitfree
云数据库地址: rm-wz99hc7z0q2izxu5jto.mysql.rds.aliyuncs.com:3306/bitfree_dev
账号: bitfree
密码:123456


## 暂时只区分了local与prod 启动参数配置即可使用本地application.yml
-Dspring.config.name=application