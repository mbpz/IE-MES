# 本机 Docker 部署

## 启动

在项目根目录执行：

```sh
cp .env.example .env
# 编辑 .env，设置两个不同的本地数据库密码。
docker compose up -d --build --wait
```

入口：http://localhost:18080/login.shtml；OrbStack 本地域名：https://app.ie-mes.orb.local/login.shtml。数据库域名不是 HTTP 页面。 。仅绑定本机地址；数据库不向宿主机开放端口。

`.env` 不进入 Git。不要在已有数据库卷上仅修改密码环境变量：MySQL 初始化变量只在空数据目录时生效。

```sh
docker compose ps
docker compose logs --tail=100 app db
docker compose down
```

`down` 保留数据；不要使用 `down -v`，除非明确需要销毁本项目数据库、上传文件和配置。

## 组成

- `db`：MySQL 8.0.44，首次从原始 35 表 SQL 初始化 `iemes_v1`，再执行独立补充脚本创建告警与两个既有测试维护页面所需的 3 张表（结构依据代码重建，无历史数据）。旧脚本含零日期，服务 SQL mode 为 NO_ENGINE_SUBSTITUTION。38 张表已在本地成功初始化。
- `app`：Maven/JDK 8 构建原始 WAR，Tomcat 9/JDK 8 运行，部署路径为 `/`。
- JDBC 文件在启动时从容器环境生成，使 Spring 和读取 JDBC 文件的 ConfigUtils 使用相同配置。
- `database`、`uploads`、`configuration`、`mailbox` 四个命名卷；上传卷首次会带入原有 Excel 模板。
- 可变 config.properties 通过 `-Diemes.config.file=/var/lib/iemes/config.properties` 读写配置卷，并复制一份普通文件供旧类路径代码读取。首次初始化使用 `example.test` 虚拟邮件地址，不会向历史收件人发送本地告警。
- 应用健康检查只证明登录入口返回 HTTP 200；不代表业务、事务或任务全部通过。

## 当前验收边界

Compose 配置、应用镜像、数据库初始化、健康检查、登录及部分业务读取、文件模板下载、定时告警已验证。逐项结果见 checklist.md。

仍待验证：业务写入、角色权限矩阵、事务回滚、上传和导入、备份恢复、IDEA 内实际导入及调试。部分实体对应表未包含在原 SQL 中；补充结构由现有代码重建，尚无历史数据。

## IDEA

通过项目根目录 pom.xml 导入。Project SDK、Maven Importer 与 Maven Runner 均选择真正的 JDK 8，不能选择 JavaAppletPlugin 中的 JRE。
已在本机验证的 JDK：`/Library/Java/JavaVirtualMachines/jdk-1.8.jdk/Contents/Home`。
在 IDEA 的 Maven 设置中将 User settings file 指向项目内的 `docker/maven-settings.xml`，以使用阿里云 Maven 镜像。

```sh
JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-1.8.jdk/Contents/Home mvn -s docker/maven-settings.xml package
```

构建输出 target/IE-MES.war。Docker 多阶段构建不依赖本机 Java/Maven。

## IDEA 远程调试配置

```sh
docker compose -f docker-compose.yml -f docker-compose.debug.yml up -d --build
```

IDEA 新建 Remote JVM Debug，Attach 模式，主机 localhost，端口 5005，选择本项目模块。调试端口只映射本机。普通启动不开放调试端口。
2026-11-05 已验证 5005 端口的 JDWP 握手；恢复普通 Compose 后该端口关闭，登录页仍返回 200。IDEA 客户端实际附加尚未验证。

本机初始化管理员用户名和密码见 `src/main/webapp/dbscript/iemes_v1.sql` 的 `-- Local development admin login` 注释；当前本地数据库密码也保存在被 Git 忽略的 `.env` 中。

## 本地告警邮件

Compose 启动 Mailpit，收件箱在 `http://127.0.0.1:18025`。应用通过内部 SMTP 服务 `mailpit:1025` 发信，使用 `example.test` 虚拟收发地址，不向真实邮箱发送。首次初始化配置卷时自动写入虚拟地址；已存在的配置卷保留已有值。SMTP 主机、端口通过 `IEMES_SMTP_HOST`、`IEMES_SMTP_PORT` 注入，非容器环境默认 `smtp.163.com:25`。本机 `.env` 可以用 `IEMES_MAILPIT_IMAGE` 覆盖 Mailpit 镜像来源；默认 `axllent/mailpit:v1.32.0`。

2026-11-05 在 ARM64 Docker 中临时将 CPU、JVM、内存告警阈值设为 0，定时任务发送 1 封邮件并写入 1 条 `ly_server_info`，随后恢复原阈值 100/100/100。
