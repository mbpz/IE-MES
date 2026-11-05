# IE-MES IDEA / Docker 迁移目标与执行记录

目标：保留全部功能，以 Maven 统一构建，支持 IDEA 开发调试及本机 Docker 完整运行。不拆微服务，不删除 Eclipse 元数据，不以关闭功能换取启动成功。

用户已批准按顺序执行。所有勾选必须有实际证据；静态盘点不等于功能通过。

## 1. 现状和功能基线
- [x] 检查初始工作区：git status --short 无输出。
- [x] 确认 Maven WAR、Java 7 编译目标、Spring MVC/MyBatis/Shiro、MySQL。
- [x] 确认本机 ARM64、Java 8、Maven 3.5.4、Docker CLI 与 Compose。
- [x] 静态清点 Controller、路由注解、JSP、数据库表、权限资源、任务及外部依赖，并记录 12 个缺失页面路由。
- [x] 对照 38 张库表与 38 个实体注解、12 个 Mapper 的字面表名及显式 selectId 字段；已记录休眠代码缺表，动态业务 SQL 仍按流程验收。
- [x] 与 MyBatis 3.2.8 原始 sources.jar 对照全部 236 个内嵌文件，确认 Plugin.joinSql 等项目定制被调用。
- [x] 在 inventory.md 区分运行已验证、待验证与仓库原有页面缺口。

## 2. 最小运行验证
- [x] 执行原始 Maven package；沙箱 DNS 失败，已获联网重试授权。
- [x] 检查并启动 OrbStack；orb start 返回 Docker engine is ready。
- [x] 完成 WAR 构建并检查 classes、资源和 JSP（120 个 JSP，Spring/JDBC/Mapper 和定制 MyBatis 类存在）。
- [x] 空数据库初始化成功：38 张表，111 条权限资源、9 个用户；管理员登录已验证。
- [x] 应用启动、登录、菜单读取和物料新增/删除代表性写入流程通过。

## 3. 兼容性修复
- [x] 固定构建插件、阿里云仓库和经验证的 JDK 8 / Maven 3.5.4、3.9.9 / Tomcat 9 / MySQL 8.0.44 版本。
- [x] 活跃下载路径改为平台无关 File 构造；Excel 上传直接从流解析，中文文件名与一行写入已在 ARM64/Linux 容器验证。
- [x] ARM64/Linux 下监控指标由 JDK 接口采集，定时告警和数据库记录已实测。
- [x] 持久化配置与 SMTP 主机、端口可注入；本地 Mailpit 捕获预警邮件。
- [x] Controller 运行时挂载 TransactionInterceptor，Spring 事务中写入后抛错会回滚；各业务异常分支仍需业务级验收。

## 4. Docker / IDEA 交付
- [x] Dockerfile、Compose、38 张表初始化、应用和数据库健康检查。
- [x] 数据库、上传文件及可变配置持久化：隔离项目恢复后数据库记录与文件校验一致。
- [x] 本机端口、启动、停止、备份恢复、调试文档：部署说明包含已实测命令，JDWP 握手已通过。
- [ ] IDEA 通过 pom.xml 导入与调试配置。
- [x] 从当前提交的 git archive 全新目录构建 WAR，397 个 Java 文件与 4 项测试通过；Docker 容器已多次重建并保持健康。

## 5. 全功能验收
- [x] 登录、退出、无角色越权拒绝与管理员菜单访问已验证。
- [ ] 基础资料、车间、BOM、工艺路线。
- [ ] 计划、订单、生产各工位、库存、质量返修。
- [ ] 看板、报表、教学评分。
- [ ] 上传下载、导入导出、中文文件名。
- [x] 监控、每分钟定时任务、Mailpit 邮件与 ly_server_info 告警记录已端到端验证。
- [x] 重建后数据/文件/配置保持：独立项目导入备份并启动应用达到健康状态。
- [ ] 记录所有失败、未验证项，完成最终差异审查。

## 已发现的风险与证据
- pom.xml：HTTP 仓库、未固定 compiler 插件，resources plugin 配置 skip。
- src/main/java/org/apache/ibatis：内嵌源码与 Maven 依赖并存。
- MessageUploadController / BackgroundController：Windows 路径拼接。
- SpringTaskController：每分钟 Sigar 采样与邮件告警；仓库未发现 Linux .so。
- PropertiesUtils：配置修改写 classpath 文件，需要持久化设计。
- Controller 存在事务注解；必须运行验证事务边界。

## 执行记录
- 2026-11-04：建立目标与清单；原始构建使用临时隔离 Maven 缓存 /private/tmp/iemes-m2，不修改用户 Maven 设置。联网构建进行中。

- 2026-11-04 23:39：指定 JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-1.8.jdk/Contents/Home，执行 mvn -B -Dmaven.repo.local=/private/tmp/iemes-m2 package，BUILD SUCCESS；396 个 Java 文件。无可执行测试，因此构建成功不代表功能验收。
- Docker info：aarch64 29.8.2。
- 修正此前风险判断：resources plugin 的 skip 配置未导致主资源丢失，实际复制 24 个资源，WAR 中验证存在。
- 数据库静态扫描 35 张表，发现 7 个待核对名称；ly_table 是动态 SQL 元数据键，不是缺表；ly_userinfo 位于示例字符串，不能直接视为运行缺表。

- 正在拉取 mysql:8.0（验证 ARM64 下旧 SQL 的兼容性，不代表已选定最终版本）和 tomcat:9-jdk8-temurin，镜像拉取任务尚待结束。
- 下一步：检查拉取结果；用独立容器和独立数据卷导入脚本；在临时部署目录覆盖本地 JDBC/邮件配置，启动原始 WAR，记录首次启动错误。不得修改已有其他项目容器或数据库。

- 镜像拉取结果：两个任务均失败，Docker 凭据助手返回 `User canceled the operation. (-128)`。未绕过凭据助手或取消操作；容器运行验证尚未开始。需要用户恢复 Docker 凭据访问后继续。

- 已创建 Dockerfile、docker-compose.yml、.env.example、docker/ContainerSupport.java 和启动脚本。`docker compose --env-file .env.example config --quiet`、JDK 8 javac、sh -n、git diff --check 均通过。实际 Compose build 正在执行；配置存在不代表运行完成。
- 数据库差异复核：ly_jayce/ly_sukey 有 Controller 与 JSP，不能删除；ly_server_info 为邮件成功后的告警保存表；ly_role_res 暂未找到业务调用；ly_res_roles 对应 XML 语句暂未找到 Java 调用。
- 已确认 MyBatis Plugin.joinSql 是依赖实体注解、表字段缓存的定制动态 CRUD 机制，必须保留。

- 本次重试已成功访问 Maven/Tomcat 镜像元数据并下载镜像层；此前凭据取消阻碍已解除。正在运行的终端任务：Compose build=94448，MySQL pull=66962。继续时必须先检查这些句柄，不重复启动。
- 为实际本地部署生成了随机密码 .env（权限 0600，Git 忽略），未输出密码。已用实际 .env 再次验证 Compose 配置通过。

- Docker 构建已完成基础镜像下载，正在 Maven 依赖/插件解析；central-https 已成功取得 sigar 与 nbm 旧依赖。MySQL pull 仍活跃，部分层重试中，尚未启动容器。
- 增加 docker-compose.debug.yml，普通与调试 Compose 配置均通过 config --quiet；远程调试实际连接待验证。

- Compose 原始镜像构建成功（任务 94448 已结束，镜像 sha256:0a17e265e952377acdd518d8cd8e9a2cfc7dbf1bcbe72ebe1537c64722d659c7）。Docker 内 Maven 3.9.9/JDK 8 完成 396 文件编译和 WAR 打包。
- 注意：上述镜像构建上下文在下载路径修改之前采集，不包含随后三处路径修复；最终验收前必须重建。路径修复已单独通过本机 Maven 离线 package（23:54:35 BUILD SUCCESS）。
- 原生库探针在本机 ARM64 复现 Sigar UnsatisfiedLinkError；监控兼容性从静态风险升级为已复现故障。

- 容器启动器验证：`docker compose run --rm --no-deps app java -version` 返回 Temurin 1.8.0_472；第二次一次性容器检查返回 CONFIG_AND_TEMPLATES_OK（config.properties 持久化链接及两份 Excel 模板存在）。不等同于用户上传/配置修改后的持久化验收。
- `git diff --check` 通过。MySQL pull 66962 仍在运行，最新输出为层重试；不得当作下载完成或重新发起同一下载。

- 2026-11-05：监控改为 JDK OperatingSystemMXBean 提供三项原有告警指标，保留旧 Sigar 工具方法及依赖。SystemInfoTest 先因缺少 usage() 失败，修复后本机 JDK 8 package 为 1 test / 0 failures；ARM64 Linux 容器内同一检查 OK (1 test)。未验证完整定时告警发送与数据库写入。
- 固定 compiler 3.13.0、war 3.4.0、surefire 3.2.5（此前容器成功构建实际采用的版本）；去除旧 HTTP 仓库声明；Docker 使用独立 Maven cache mount。更新后镜像构建任务 27039 进行中。
- MySQL 下载 66962 已以 Docker Hub Client.Timeout 失败；确认引擎配置了多个镜像代理，没有修改用户 Docker 配置。固定镜像重试任务 81552 进行中。

- 增加独立 docker/sql/002-runtime-tables.sql，按现有消费者补充 ly_server_info / ly_jayce / ly_sukey 三表，不修改原 SQL、不伪造历史数据。Compose 挂载新增脚本，健康检查增加最后补充表 ly_sukey 查询；config --quiet 通过，SQL 执行仍待数据库镜像。
- 任务句柄最新确认：27039 正在下载 Maven 插件依赖；81552 仍活跃尚无新输出。

## 当前恢复点（2026-11-05）
- 中断前句柄 27039 / 81552 已不存在；通过 docker images 确认新镜像 ie-mes-app:latest ID 854bb0ecc740 已构建，MySQL 镜像不存在，compose ps -a 无业务容器。
- 对实际镜像内 SystemInfo 执行回归检查 OK (1 test)；javap 确认 usage() 新接口存在。
- 本机直接访问 registry-1.docker.io:443，10 秒连接超时。尝试 Google 官方缓存 mirror.gcr.io/library/mysql:8.0.44 同样 context deadline exceeded，任务 37382 已结束。当前无下载任务需要等待。
- 同一镜像网络阻碍已跨多个执行轮次复现；等待可用代理/认可的镜像源或 OrbStack 网络恢复。未修改全局 Docker 配置，未更换数据库实现。
- 恢复后首先 pull mysql:8.0.44，再 docker compose up -d --wait，读取初始化与应用日志。数据库尚未初始化，补充 SQL 尚未执行。之后继续登录/权限/业务/邮件/事务/持久化/调试验收，不得以镜像成功代替全功能通过。

## 2026-11-05 VPN 恢复后的证据
- 虚拟网卡模式下 Docker Hub 返回 HTTP 401（正常鉴权入口），mysql:8.0.44 镜像拉取成功。
- 避开已有 agentra-server 的 8080 宿主机端口，项目映射改为 127.0.0.1:18080。
- MySQL 原始 35 表与补充 3 表初始化成功；`ly_resources` 111 条，`ly_user` 9 条。
- 首次应用启动由 Common.java:130 空指针阻止。PropertiesUtils 的 MyBatis 资源路径带 `/` 且容器 config.properties 是 webapp 外部软链接。新测试先失败，现改为显式外部配置文件路径与普通 classpath 文件；4 个 Maven 测试通过。
- 第二次应用启动暴露 MySQL Connector/J 5.1.26 对 MySQL 8.0.44 字符集映射的 BigInteger/Long 类型异常。升至 Connector/J 8.0.33，改用 `java.sql.Connection`，镜像重建后 Spring 启动完成。
- `docker compose ps`：app、db 均 healthy。GET `http://127.0.0.1:18080/login.shtml`、`http://app.ie-mes.orb.local/login.shtml`、`https://app.ie-mes.orb.local/login.shtml` 均返回 200；HTTPS 证书无需跳过验证。数据库域名不是网页入口。
- 按用户要求，原始 SQL 的管理员 insert 旁加 `-- Local development admin login` 明文注释并同步种子哈希；仅本地数据卷中的 admin 密码也同步设置。本地 `.env` 保留 `IEMES_ADMIN_PASSWORD`，不入 Git。POST `/login.shtml` 返回 302 `index.shtml`，登录成功。
- Maven 项目设置改为阿里云镜像；Docker Maven 3.9.9 与本机 Maven 3.5.4 均构建通过（compiler 插件固定 3.8.1）。
- 尚未验收：各业务流程、完整权限矩阵、文件读写与重建保持、定时告警和邮件、事务回滚、IDEA 远程调试连接。

- 2026-11-05 后续：按用户要求，SQL 管理员行旁新增本地明文账号注释，同时更新初始化哈希、当前本地数据库与 Git 忽略的 `.env`。SQL 注释与 `.env` 一致；容器重建后 admin POST 登录仍成功。
- WAR 排除 `dbscript/**`；jar 内容和运行中容器均确认 SQL 不在应用 Web 根目录。Docker 重建后 app/db 均 healthy，OrbStack HTTPS 登录页返回 200。
- 鉴权抽查：匿名 `/index.shtml` 跳转登录；管理员登录后首页、物料、计划、看板页面均 HTTP 200。完整业务写入与权限矩阵仍待验收。

- 2026-11-05 看板回归：无头 Chrome 在修复前捕获 `app.v1.js` 注入 `http://www.lanyuanoss.com/lanyuan.shtml`，并确认 `layui/css/modules/layer/skin/style.css` 由 `common.jspf` 的 `layer.config` 发起；图钉菜单计算样式为白底白字。移除旧外部脚本和无效皮肤配置，菜单设深色文字；重新构建并重建 Docker 应用后，Chrome 网络/日志中无上述请求，弹层变为白底 `rgb(34,34,34)` 文字。`node --check`、Docker Maven 构建和 `git diff --check` 通过，app/db 均 healthy。

- 2026-11-05 文件持久化验证：管理员 HTTP 下载 userInfo.xlsx 9348 字节、masterData.xlsx 17021 字节，与仓库原文件 SHA-256 一致；两个不同一次性应用容器中上传卷和配置卷的标记保持一致，测试标记已删除。
- 2026-11-05 本地邮件告警：Compose 增加 Mailpit（本机 18025），应用 SMTP 主机/端口由环境传入，原 smtp.163.com 为非容器环境默认值。初始容器配置使用 example.test 虚拟邮箱。当前既有配置卷已设置为虚拟邮箱。暂将 cpu/jvm/ram 阈值设为 0，等待 SpringTaskController 每分钟运行，Mailpit API 从 0 变 1，邮件主题含“预警”，ly_server_info 从 0 变 1；阈值恢复原值 100/100/100。由于 Docker Hub 临时超时，本机 .env 的 IEMES_MAILPIT_IMAGE 指向可达镜像代理；项目默认仍为官方镜像名。

- 2026-11-05 IDEA 调试链路：调试 Compose 配置解析成功，临时启动 app/debug 并获得健康状态；`127.0.0.1:5005` 返回完整 `JDWP-Handshake`。恢复普通 Compose 后 5005 关闭，登录页 HTTP 200；IDEA GUI 内实际导入/附加仍待验证。

- 2026-11-05 隔离备份恢复证据：mysqldump 首次因 PROCESS/tablespace 查询报错，增加 `--no-tablespaces` 后无错误完成。导出数据库、上传卷与配置卷到权限受限的 `backups/2026-11-05-restore-verified/`，SHA256SUMS 三项均 OK。`ie-mes-restore-check` 是新建测试项目；恢复后表数 38、用户 9/ID 合计 219、资源 111/ID 合计 10807、工单 12/ID 合计 718、告警 1 与当前库一致。上传 3 文件、配置 1 文件 SHA-256 完全相同，恢复出的独立应用达到 healthy；随后清理了仅测试项目的容器和卷，当前 `ie-mes` 未执行 down -v。Mailpit 历史不在业务备份范围。

- 2026-11-05 干净构建：`git archive HEAD` 解包到 `/private/tmp/iemes-clean-LJrA3D`，JDK 8 + 项目阿里云 Maven settings 执行 `mvn package`，BUILD SUCCESS，4 tests / 0 failures；新 WAR 不含 dbscript。
- 2026-11-05 MyBatis 原始源码差异：236 个内嵌文件中 198 个原样、18 个只有空白变化、20 个存在非空白变更；项目 `Plugin.joinSql` 不是上游 API，`PagePlugin` 运行时调用。详细见 inventory.md。

- 2026-11-05 权限实测：数据库 30 个 type=1 菜单项中 26 个 `.shtml` 页面管理员 GET 均返回业务页面，4 个为分组占位路径。临时无角色账号在原配置下能看到 `/role/list.shtml`，定位 `spring-shiro.xml` 把 `/** = authc` 排在动态 `perms[...]` 之前；删除前置通配项，保留 `ChainDefinitionSectionMetaSource` 在末尾追加的兜底。修复后临时账号仍可登录但角色页呈现拒绝内容，账号和登录记录均已清理；管理员 26 个菜单页面仍全部可访问，退出后受保护页面回到登录页。

- 2026-11-05 物料 CRUD 抽查：管理员 POST `/item_resource/item_addEntity.shtml` 返回成功，`ly_item` 出现 1 条临时物料；POST `/item_resource/item_deleteEntity.shtml` 返回 success，数据库该物料数归零。未将这次单记录写入扩展为所有业务流程或事务回滚的证明。

- 2026-11-05 当前数据库结构复核：原 SQL 35 张表 + 补充 SQL 3 张表、38 个实体表注解；`ly_role_res` 注解无表、`ly_resource_type` 有表无注解，`ly_res_roles` 只在未见调用的旧 Mapper 语句中。详情见 database-gaps.md；列级业务映射仍待逐项核对。

- 2026-11-05 Excel 导入端点：用两份中文文件名、仅有表头的工作簿（用户 1 Sheet、主数据 8 Sheet）登录后 POST 到 `/message/studentInfoUpload.shtml` 与 `/message/masterDataUpload.shtml`。修复前两者均执行到容器临时文件写入但最终 HTTP 404；缺少 `@ResponseBody`。补注解后两者均 HTTP 200、JSON `result=true`，`ly_user`/`ly_item`/`ly_workcenter`/`ly_process_route` 行数不变，临时文件已清理。含真实数据行的导入和错误提示行为仍待验证；代码当前在容器 CWD 留下上传原文件，不在持久化卷。

- 2026-11-05 Excel 文件路径修复：原导入实现将上传原文件写到 Tomcat 工作目录且不清理；改为 `WorkbookFactory.create(BufferedInputStream)` 直接解析请求流，调用方在请求结束时关闭流。ARM64/Linux 容器中，中文文件名的用户空模板及主数据单行工作簿均 HTTP 200 JSON `result=true`；主数据工作中心新增 1 行，随后删除，Tomcat 工作目录无遗留 XLSX。含实际用户数据的导入、异常提示和所有文件类型仍待验收。

- 2026-11-05 事务诊断：仅在运行容器临时放置受登录保护的诊断 JSP，读取 MVC 子容器中 `itemController`，确认其 AOP advisor 为 `org.springframework.transaction.interceptor.TransactionInterceptor`。另通过根容器 `transactionManager` 的 `TransactionTemplate` 写入临时 `ly_item` 后主动抛出 `IllegalStateException`，HTTP 输出 `forcedFailure=true;remainingRows=0`，证明本地 InnoDB/Spring 事务回滚基础链路有效。诊断 JSP 已删除，数据库无测试残留；这不证明每个控制器的业务异常分支都正确回滚。

- 2026-11-05 静态与运行页面基线：40 个 Controller、306 处 RequestMapping 注解、120 个 JSP、12 个 Mapper XML、1 个 Scheduled、38 张初始化表、111 条权限资源。12 处固定 JSP 返回路径缺文件，管理员 GET 均 HTTP 404；`git rev-list --all --objects` 未发现对应历史 JSP。详见 inventory.md。

- 2026-11-05 报表兼容性：`ReportController.getReportData` 的 9 个 reportMethod 中，首次 `getScheduleReportData`（即使用有效工单号 gd01）返回 500，日志为数据库用户对硬编码 `iemes.ly_shoporder_sfc` 的 SELECT 拒绝。`report-mapper.xml` 共 21 处 `iemes.ly_*` 前缀改为当前 JDBC 库内相对表名；Docker 重建后 9 种查询均 HTTP 200 / JSON result=true，调度报表返回 12 条。看板与教学交互仍需单独验收。

- 2026-11-05 教学评分与通用表格查询：页面脚本里 28 个无附加 URL 参数的 `jsonUrl` 接口首次 27 个正常、`/point_grade/point_grade_findByPage.shtml` HTTP 500。日志显示 MySQL 8 把动态 SELECT 中未转义的 `function` 列当作关键字。检查当前库 294 个列名均为简单标识符。最初在 `ConfigUtils` 缓存中加反引号虽使 SELECT 通过，却让动态 WHERE 的字段匹配失效；已改为保留原始缓存字段，只在 `Plugin.joinSql` 输出 SELECT 字段时加引号。教学列表筛选由错误的 33 条恢复为与 SQL 一致的 11 条，28 个表格接口全部 HTTP 200 / 可解析 JSON。

- 2026-11-05 评分写入修复：因字段缓存被加引号造成动态筛选失效，首次测试临时考试时误删原始考试 ID 45；立即从已验证备份的 `ly_exam` INSERT 恢复，原评分 33 行未变化，后续反复核对 ID 45 存在。随后修复 `Plugin.joinSql` 在 BATCHSAVE 生成 SQL 时对 `function` 关键字列加反引号；临时考试新增 1 考试/1 评分成功。替换旧评分原来用新 JSON 项目数判断旧 ID 分隔符，导致 SQL 语法错误并删除考试；现在按旧列表自身拼接 ID，给整个保存方法加 `@Transactional`，空评分列表跳过批量 INSERT。临时考试实测新增 1/1、替换 1/2、提交带单引号的无效评分值触发 HTTP 500 后原有 1/2 及评分 ID 不变、清空评分得到 1/0，最后彻底清理临时数据。带单引号的用户值仍被旧 SQL 拼接拒绝，需后续参数化修复，不能称为所有输入均通过。

- 2026-11-05 字段与 Mapper 复核：实际库 38 张表、294 列，38 个 `@TableSeg` 表名中 `ly_role_res` 无表、`ly_resource_type` 有表无实体；12 个 Mapper XML 引用 26 个字面 `ly_*` 表名，只有 `resources-mapper.xml` 的 `ly_res_roles` 不存在且对应两个 Mapper 方法未见 Java 调用。显式 `selectId` 字段片段与相关实际表字段相符（log-mapper 字段在 ly_log 中均存在）。运行中的 28 个表格查询与 9 种公共报表方法通过；动态 SQL 的特定业务条件仍需流程级验收。
