## bitfree
极海开发者社区

## DB配置需要在本地创建 db.json，内部格式
{
"url": "jdbc:mysql://127.0.0.1:3306/bitfree?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai&allowMultiQueries=true",
"username" : "xxx",
"password" : "xxx"
}

## 暂时只区分了local与prod 启动参数配置即可使用本地application.yml
-Dspring.config.name=application