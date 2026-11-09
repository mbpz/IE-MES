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

`docker/sql/002-runtime-tables.sql` 从 SpringTaskController 和 jayce/sukey Controller/JSP 的字段消费者重建三个缺失表。它不覆盖原始脚本，不声称恢复了历史数据；MySQL 已完成 38 表初始化、告警写入与代表性业务查询。未为无调用证据的旧 RoleResFormMap / XML 语句盲目补表。

## 2026-11-05 当前结构复核

原 SQL 35 张表加补充 SQL 3 张表，共 38 张；`@TableSeg` 标注表名 38 个。仅 `RoleResFormMap` 标注的 `ly_role_res` 不在初始化脚本中；`ly_resource_type` 有表但无对应标注实体。`resources-mapper.xml` 中 `deleteResourcesRole` 与 `addRoleRes` 两条语句指向不存在的 `ly_res_roles`，全仓库未找到 Java 调用点，先按休眠代码记录。`ly_table` 是动态 CRUD 元数据键而非实体表，`ly_userinfo` 出现在分页插件的示例字符串。暂不据此生成推测性的表。

## 报表旧库名前缀

`report-mapper.xml` 原有 21 处 `iemes.ly_*` 固定库名前缀，与当前初始化库 `iemes_v1` 不一致，曾使调度报表返回 500（SELECT 权限错误）。已改为不带库名的表引用，九种公共报表查询在当前 Docker 库中均返回成功 JSON。

## MySQL 8 关键字列

`ly_teaching_testinfo.function` 在原 MyBatis 动态 SELECT 字段清单中未加引号，导致教学评分列表 HTTP 500。最终修复保留 `ConfigUtils` 缓存中的原始列名供动态筛选匹配，仅在 `Plugin.joinSql` 输出 SELECT 字段与 BATCHSAVE 的 INSERT 字段时加反引号。当前 294 个列名符合简单标识符形式，修复后教学列表筛选结果与数据库一致，28 个表格数据接口返回成功 JSON；评分新增、替换与清空已在临时考试中验证。

2026-11-09 生产终验进一步发现：考试启用时，评分服务按 `function` 查询和写入分数会使工单新增返回错误，但工单已入库。现将动态 `findByNames` / `findByPage` 的字段条件，以及单行新增/修改的字段名也加反引号。隔离库复验工单新增返回 `success`；启用考试并配置“计划工单维护”3 分后，新增返回 `score=3`，`ly_grade_calculate` 写入对应记录。主栈 34 个菜单页和九种报表在部署后回归通过。此改动仅解决关键字列名；旧动态 SQL 的值拼接仍应作为后续安全改造项目。

## 字面表名与字段片段复核（2026-11-05）

12 个 Mapper XML 共引用 26 个字面表名，只有 `ly_res_roles` 缺表；对应 `deleteResourcesRole`/`addRoleRes` 未见 Java 调用。各 `<sql id="selectId">` 中可直接识别的简单字段均能在相关实际表中找到；`log-mapper.xml` 的 8 个字段均属于 `ly_log`。当前库共 294 列。此静态检查不覆盖运行时动态拼接的所有条件，后者需业务流程验收。

## 质量种子数据关联缺口（2026-11-05）

当前站点 `*` 有 3 条打开的 `ly_sfc_nc` 不良记录，但与同站点 `ly_nc_code` 的匹配数为 0。`findNCBySFC` 的多表查询因此无法渲染 `nc_handle_info.jsp` 中的条件式处置表单；此为种子数据关联缺口，不是 JSP 文件不存在。未补造缺失的代码/组记录。

2026-11-05 在独立测试库为现有 SFC 补齐同站点物料、代码、代码组及工艺步骤后，质量详情页渲染产品信息和处置表单，维修与报废通过隔离流程验证；主库没有补造这些历史数据。
