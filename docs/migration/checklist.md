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

- 2026-11-05 隔离业务验收：新建 `ie-mes-acceptance` 独立 Compose 项目，从已验证备份恢复数据库，应用在本机 18081 达到 healthy，原 `ie-mes` 一直独立运行。管理员 API 新增 BOM 主表/物料关系各 1 行、工艺路线/步骤各 1 行；新工单从状态 0 创建、下达状态 1 且生成 SFC/步骤各 1 行。`getShoporderInfoByNo` 首次因 `LocalDateTime` 强转 Date 失败，已在 FormMap.getDate 兼容 Java 8 日期对象，重建后既有工单的起止时间为字符串，缺失工单返回 result=false。
- 2026-11-05 两步生产链：开始按钮原先将工单号放错到 SFC 查询对象，导致当前工单状态未从 1 变 2；修复后当前工单独自变 2。末工序原先先统计当前尚未完成的 SFC，且用忽略表字段的 findByWhere 查询全库；修复后改为按工单/站点 findByNames 并排除当前即将完成的 SFC。该事务中通用 MyBatis Mapper 的动态 SQL 在生成本地缓存键之后才重写，曾把 RoutingStepFormMap 缓存结果返回给 ShopOrderSfcFormMap 查询；增加 MyBatis `localCacheScope=STATEMENT` 后，隔离工单末工序从状态 2 到 3，SFC=2、OpA/OpB 步骤都为 2。更改后 28 个表格数据接口与 9 种公共报表查询全部复测通过。
- 2026-11-05 库存/质量隔离验证：物料接收 API 生成 1 条库存记录，receive=1；生产中 SFC 记录不良后 `ly_sfc_nc` 生成 1 条、步骤和 SFC 均为状态 4；维修 API 生成 1 条 `ly_nc_repair`，不良记录状态 1、SFC 恢复状态 1。`nc_handle_info.jsp` 实际包含条件式处置表单；本次测试 SFC 的关联数据不满足条件，前端处置详情仍需完整关联数据验收。完整工位、报废及库存消耗分支仍未验证。

- 2026-11-05 更正质量详情页判断：JSP 有完整 `<c:if test="${exist==true}">` 表单，隔离测试记录的 `findNCBySFC` 关联结果为空才只输出页面骨架。当前种子库 site=`*` 的 3 条打开不良记录与同站点 `ly_nc_code` 匹配数为 0；不能用这些数据证明页面本身损坏，需完整关联样本验收。

- 2026-11-05 Chrome 页面巡检：在独立无头 Chrome 中通过 HTTPS 登录后逐一用 `#loadhtml` 加载 26 个页面型菜单路由；DevTools Network 实际观测 26/26 个对应页面响应为 HTTP 200，Runtime 未报告脚本异常，未捕获失败资源请求。审计只覆盖页面加载，不代表每页按钮/表单的业务结果。Chrome 临时进程已关闭。
- 2026-11-05 复审跟进：用户明确要求保留 SQL 中管理员示例密码明文注释。Docker 启动改为要求独立 `IEMES_ADMIN_PASSWORD`，只在数据库仍为示例哈希时替换；当前实例的 `.env` 已生成新的独立随机密码。新密码实际登录成功，示例密码被拒绝，重启后新密码仍可登录。备份示例改为 `umask 077` 且暂停应用写入后复制数据库和卷；实际导出完整 SQL 与两个可读取归档，目录 0700、文件 0600，应用恢复 healthy。导入接口对损坏 Excel 与空文件均 HTTP 200 / JSON `result=false`，前端显示错误消息。原用户模板的 40 行全空格式行原先触发空指针，现跳过全空行；上传原用户模板与清空数据行的 8-sheet 主数据模板均 HTTP 200 / `result=true`。用户导出 `/user/export.shtml` 返回 HTTP 200、4608 字节 OLE2 XLS 附件。含实际用户数据、主数据全部样例行以及导入中途失败的原子性仍待验证；主数据和用户导入代码内存在逐行异常吞掉的旧逻辑。
- 2026-11-05 独立新库导入验收：`ie-mes-import-check` 用原始 SQL 新建 38 张表，应用在 18081 启动，`.env` 独立管理员密码可登录，公开示例密码被拒绝。中文文件名的单行用户工作簿返回 `result=true`，`ly_user` 新增 1 条且 `ly_user_role` 与预期角色关联新增 1 条。原始 `masterData.xlsx` 八个 Sheet 的样例行返回 `result=true`；与主库相同种子计数相比，工作中心 +2、操作 +2、工艺路线 +1、物料 +4、BOM 明细 +1、不良代码组 +2、不良代码 +2；再次导入计数不变。此结果不证明所有任意输入均正确，特别是代码仍有逐行异常吞掉、可能部分成功的问题。测试应用、数据库及四个测试卷已清理，主库未执行测试写入。
- 2026-11-05 导入原子性：独立 `ie-mes-import-atomicity` 新库先用两行用户工作簿复现旧行为：第二行角色不存在，接口仍返回 `result=true`，数据库留下 2 用户、1 角色关联。给两个导入入口加事务，移除逐行吞错；同类新工作簿返回 `result=false`，新用户与关联均为 0。原始八表主数据样例末尾加缺失字段的错误行，接口返回 `result=false`，工作中心/操作/资源/工艺路线/物料/BOM/不良代码组/不良代码计数保持种子值 15/17/17/4/20/2/4/19。随后正常用户单行与原始主数据模板在新事务下均返回 `result=true`，用户及角色关联各 +1、各主数据计数达到 17/19/17/5/24/3/6/21。只覆盖上述两类导入的代表性失败与成功；其余数据组合仍需验证。测试容器与卷已清理，主栈重建后 app/db/mailpit 均 healthy；主栈管理员登录成功、两种损坏文件导入均返回 `result=false`。
- 2026-11-05 IDEA 与旧页面源复查：`/Applications`、`~/Applications` 和 Spotlight 均未发现 IntelliJ IDEA；因此只有 Maven 导入说明与 JDWP 握手证据，IDEA GUI 验收仍未完成。Spotlight 与常见本地工程/备份目录中未找到缺失的 `repair_pod.jsp` 等页面；`routing_edit.jsp` 只在现仓库及构建输出存在，内容却是站点编辑表单，不能冒充缺失的工艺路线编辑页。12 个原仓库 JSP 缺口仍需原 Eclipse 工作区或功能重建。
- 2026-11-05 隔离质量流程：`ie-mes-qc-check` 用原始数据新建库，只在该库补齐站点 `*` 的 `sj` 物料、`ncc02` 不良代码、`nc01` 代码组和 `gy001` 工艺步骤，并把 SFC 的状态置为待处置。主库 `/qc/getNcHandleSFCData.shtml?sfc=22` 返回空骨架；隔离库同路径返回产品信息、中文物料/不良描述和处置表单。隔离维修 SFC `22` 返回成功，原工序状态 2、SFC 状态 1、生成下一工序；再次查询不再显示未处理表单。报废 SFC `3333` 返回成功，原工序和 SFC 状态 3、不良状态 1。
- 2026-11-05 质量异常与终结修复：SQL 注释规定 `repair_type` 0=维修、1=报废，旧维修实现也写 1；旧 `NcHandleRepair` 在后段抛受检异常时，HTTP 报错但仍将工序改成 2、不良改成已处理并插入维修记录。给维修/报废事务增加受检异常回滚，修正维修类型和 `site` 筛选；同样的隔离失败样本返回 `status=false`，工序保持 4、不良保持未处理且无新增处置记录，成功维修为 `repair_type=0`。旧报废工单终结判断恒为假，隔离工单其余 4 个 SFC 均已完成时，报废最后一个 SFC 后工单仍为 2；改为按当前工单/站点查询并排除正在报废的 SFC 后，工单变 3。构造报废后段受检异常时，工序、不良和处置记录也保持回滚。未知 SFC、非法处置方式与空维修目标由 HTTP 200 / 业务 `status=false` 明确返回，正常维修仍成功。以上都是隔离数据验证，不等于所有质量分支通过。
- 2026-11-05 质量修复部署复核：主栈应用按当前代码重建并达到 healthy；管理员登录成功，主库未知 SFC 处置返回 HTTP 200 / `status=false` 和明确提示。隔离 `ie-mes-qc-check` 容器及测试卷已删除；主库仍为 9 用户、12 工单、站点 `*` 的 3 条打开不良记录，`sj` 物料在该站点仍不存在，未将隔离关联样本写入主库。
- 2026-11-05 物料消耗看板对账：旧查询在关联多条 BOM 明细后才汇总接收量，站点 `*` 的 LTPCB 实收 3、已用 1，却显示接收 6、结存 5；未消耗的 R82PZ 在 `UNION ALL` 两侧各出现一行，整个接口 5 行仅 4 个物料。改为接收、消耗、BOM 阈值分别按站点和物料聚合再连接；因一物料多条 BOM 明细的阈值可能不同，暂对上限和安全库存数均取最大值，延续该库原先观测到的显示值，业务口径待确认。主栈重建 healthy 后看板 HTTP 200、4 行 4 个物料；LTPCB 接收/已用/结存为 3/1/2，R82PZ 只出现一行。独立 `ie-mes-inventory-check` 新库中，接收一个 LTPCB 物料 SFC 后看板由 3/1/2 变成 4/1/3；装配一次变成 4/2/2，重复装配返回 `status=false`，两张业务表对该 SFC 各只有一行。隔离容器与卷已清理，主库未写入本次测试收货/消耗。
- 2026-11-05 原用户页面 Chrome 复验：独立无头 Chrome 通过 OrbStack HTTPS 登录，首页工单生产预览表实际渲染 5 行，订单开始/完成时间均为有效 `YYYY-MM-DD`，无 `NaN`；随后在 `#loadhtml` 加载车间库存报表与车间物料消耗看板，报表 ECharts 画布存在，看板表格 4 行。工单列表、报表页面及 `getReportData`、看板页面及 `getMaterialConsumptionDataFindByPage` 的实际 Network 响应均 HTTP 200；未捕获 HTTP 4xx/5xx、Runtime 异常、console error、Log warning/error 或 Network loadingFailed。仅覆盖这些页面的加载和数据展示，未代替所有交互验收；临时 Chrome 进程与资料目录已删除。
- 2026-11-05 看板复审回归：新聚合查询起初用内连接要求 BOM 明细，且将文本阈值强转为无符号整数。在 `ie-mes-kanban-review` 独立新库构造三个已接收物料：无 BOM 的 `REVIEW_NO_BOM` 在旧版新查询中消失；小数阈值 9.75/1.5 被改为 9/1；非数字 `N/A` 被改为 0/0。现改为左连接 BOM，只有匹配有符号十进制格式的阈值才按十进制数聚合，无效文本显示为空。隔离看板与库存报表接口均 HTTP 200，三个物料全部出现，阈值分别为 null/null、9.75/1.5、null/null；原 LTPCB 仍为接收/已用/结存 3/1/2。主栈重建 healthy 后，看板 4 行 4 物料，库存报表 `getInventoryData` 返回 4 行。隔离容器和卷已清理。BOM 表仍以 VARCHAR 存阈值且表单未验证数字，写入验证与多 BOM 阈值业务口径仍待确定。
- 2026-11-05 缺失 JSP 外部来源复核：在本仓库 Git 历史之外，检查了公开 IE-MES 源码树 `doiiiiiit/IE-MES` d9c6b52、`victor-zhou/IE-MES` f4f49c2、`zdc_admin/ie-mes` 18595dc 与 GitHub fork `Afunny/IE-MES` 7839d46；按 12 个缺失页面文件名扫描，均未找到原 JSP。临时外部源码副本已删除；仍需用户原 Eclipse 工作区或另行重建。
- 2026-11-05 看板阈值边界复核：复审指出无界数字文本超过 MySQL `DECIMAL(65,30)` 时会被舍入或截断；现只将最多 35 位整数、30 位小数的有符号数字转换，其他值显示为空。MySQL 实测 `9.75`、`-1.5`、`+1.5`、`.5`、35 位整数和 30 位小数均匹配；36 位整数、31 位小数与 `N/A` 均被拒绝。BOM 写入端仍允许任意文本，后续需与业务方确定阈值校验和多 BOM 聚合口径。
- 2026-11-05 系统管理权限隔离复现：独立 `ie-mes-permission-check` 新库导入 `用户1` 角色测试账号，其角色 0 条资源授权。旧配置下 `/role/list.shtml` 和 `/user/list.shtml` 跳转拒绝页，但 `/role/findByPage.shtml` 可返回角色 JSON、`/user/export.shtml` 可导出 XLS、`/message/studentInfoUpload.shtml` 可成功处理空工作簿。旧 `/user/editPassword.shtml` 还可凭请求中他人 ID/账号修改另一测试用户密码，已在隔离账号中复现。所有测试数据仅在独立库。
- 2026-11-05 系统管理权限修复：在动态资源规则后、全站登录兜底前为 user/role/message 的数据、写入、导入/导出端点补对应权限；自助改密保留登录可用，但服务器从当前会话确定用户 ID/账号，只更新密码与盐，提交他人 ID 直接返回 `error`。隔离账号复测：无资源授权时角色数据、用户导出与导入都跳转拒绝页；管理员角色页面、角色 JSON、用户 XLS 和空工作簿导入仍成功。另建立只含 `account_add` 的测试角色：可访问新增用户页及用户导入，不能访问用户列表、角色列表或主数据导入页。跨账号改密失败且双方密码不变，正常自助改密成功。此项覆盖系统管理代表性矩阵，其他业务模块的未映射 API 仍需系统审计。
- 2026-11-05 权限修复部署复核：主栈按当前源码重建后 app/db/mailpit 均 healthy；未登录访问角色数据接口返回登录页，管理员可登录并读取 2 条角色数据。隔离 `ie-mes-permission-check` 应用、数据库及测试卷已全部清理；主库未建立本次测试账号。
