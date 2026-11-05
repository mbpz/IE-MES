# IE-MES IDEA / Docker 迁移目标与执行记录

目标：保留全部功能，以 Maven 统一构建，支持 IDEA 开发调试及本机 Docker 完整运行。不拆微服务，不删除 Eclipse 元数据，不以关闭功能换取启动成功。

用户已批准按顺序执行。所有勾选必须有实际证据；静态盘点不等于功能通过。

## 1. 现状和功能基线
- [x] 检查初始工作区：git status --short 无输出。
- [x] 确认 Maven WAR、Java 7 编译目标、Spring MVC/MyBatis/Shiro、MySQL。
- [x] 确认本机 ARM64、Java 8、Maven 3.5.4、Docker CLI 与 Compose。
- [ ] 清点每个 Controller、路由、页面、表、权限、任务及外部依赖。
- [ ] 核对 SQL 与实体和 Mapper 的表字段差异。
- [ ] 核对项目内 MyBatis 源码与依赖版本的定制行为。
- [ ] 标注已确认功能、待验证功能与原有缺陷。

## 2. 最小运行验证
- [x] 执行原始 Maven package；沙箱 DNS 失败，已获联网重试授权。
- [x] 检查并启动 OrbStack；orb start 返回 Docker engine is ready。
- [x] 完成 WAR 构建并检查 classes、资源和 JSP（120 个 JSP，Spring/JDBC/Mapper 和定制 MyBatis 类存在）。
- [x] 空数据库初始化成功：38 张表，111 条权限资源、9 个用户；管理员登录已验证。
- [ ] 应用启动、登录和代表性读写流程通过。

## 3. 兼容性修复
- [x] 固定构建插件、阿里云仓库和经验证的 JDK 8 / Maven 3.5.4、3.9.9 / Tomcat 9 / MySQL 8.0.44 版本。
- [ ] 修复 Linux 文件路径、文件名与编码兼容性。
- [x] ARM64/Linux 下监控指标由 JDK 接口采集，定时告警和数据库记录已实测。
- [x] 持久化配置与 SMTP 主机、端口可注入；本地 Mailpit 捕获预警邮件。
- [ ] 核对事务生效与失败回滚。

## 4. Docker / IDEA 交付
- [x] Dockerfile、Compose、38 张表初始化、应用和数据库健康检查。
- [ ] 数据库、上传文件及可变配置持久化。
- [ ] 本机端口、启动、停止、备份恢复、调试文档。
- [ ] IDEA 通过 pom.xml 导入与调试配置。
- [ ] 干净构建及容器重建验证。

## 5. 全功能验收
- [ ] 登录退出、角色权限、越权拒绝。
- [ ] 基础资料、车间、BOM、工艺路线。
- [ ] 计划、订单、生产各工位、库存、质量返修。
- [ ] 看板、报表、教学评分。
- [ ] 上传下载、导入导出、中文文件名。
- [x] 监控、每分钟定时任务、Mailpit 邮件与 ly_server_info 告警记录已端到端验证。
- [ ] 重建后数据/文件/配置保持，备份恢复验证。
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
