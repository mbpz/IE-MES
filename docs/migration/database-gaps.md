# 数据库静态差异

初始化脚本：35 张表。以下是代码引用但脚本未建的名称，需排除注释并运行验证，不自动创建推测表结构。

## ly_jayce
- src/main/java/com/iemes/entity/JayceFormMap.java

## ly_res_roles
- src/main/resources/mappings/resources-mapper.xml

## ly_role_res
- src/main/java/com/iemes/entity/RoleResFormMap.java

## ly_server_info
- src/main/java/com/iemes/entity/ServerInfoFormMap.java

## ly_sukey
- src/main/java/com/iemes/entity/SukeyFormMap.java

## ly_table
- src/main/java/org/apache/ibatis/plugin/Plugin.java
- src/main/java/org/apache/ibatis/session/Configuration.java
- src/main/java/com/iemes/util/Common.java
- src/main/java/com/iemes/plugin/PagePlugin.java

## ly_userinfo
- src/main/java/com/iemes/plugin/PagePlugin.java

## 本地补充结构

`docker/sql/002-runtime-tables.sql` 从 SpringTaskController 和 jayce/sukey Controller/JSP 的字段消费者重建三个缺失表。它不覆盖原始脚本，不声称恢复了历史数据；待 MySQL 就绪后验证插入、查询及类型兼容性。未为无调用证据的旧 RoleResFormMap / XML 语句盲目补表。
