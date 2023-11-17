## bitfree
bitfree 后端部分

## 本地启动需安装MySQL/JDK

### 如果要使用本地DB，请设置application.yml变量 datasource: config-path: ${本地db.json所在目录}

#### db.json格式如下：
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
