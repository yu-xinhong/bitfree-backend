## bitfree
bitfree 后端部分

## 本地启动需安装MySQL/JDK
## 执行resources下的ddl
bitfree_2023-10-14.sql

## DB配置需要在本地datasource.config-path配置目录创建 db.json，内部格式

{
"url": "jdbc:mysql://127.0.0.1:3306/bitfree?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai&allowMultiQueries=true",
"username" : "xxx",
"password" : "xxx"
}

## 暂时只区分了local与prod 启动参数配置即可使用本地application.yml
-Dspring.config.name=application