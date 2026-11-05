# 本机 Docker 部署

## 启动

在项目根目录执行：

```sh
cp .env.example .env
# 编辑 .env，设置两个不同的数据库密码和独立的管理员密码（至少 16 字符）。
docker compose up -d --build --wait
```

入口：http://localhost:18080/login.shtml；OrbStack 本地域名：https://app.ie-mes.orb.local/login.shtml。数据库域名不是 HTTP 页面。仅绑定本机地址；数据库不向宿主机开放端口。

`.env` 不进入 Git。不要在已有数据库卷上仅修改数据库密码环境变量：MySQL 初始化变量只在空数据目录时生效。管理员密码仅在数据库中仍为公开示例哈希时，由应用启动器替换；以后通过页面修改密码不会被容器重启覆盖。

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

Compose 配置、镜像构建、数据库初始化、登录、代表性业务写入和权限、基础事务回滚、模板下载与单行导入、定时告警，以及隔离环境中的备份恢复均已验证。逐项范围见 checklist.md。

仍待验证：所有业务分支、完整角色权限矩阵、实际用户数据导入和导出、IDEA 内实际导入及调试。12 处固定页面路径缺少对应 JSP；部分补充表没有历史数据。

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

SQL 的 `-- Local development admin login` 注释保留了原始示例账号和明文密码，方便识别种子数据。Docker 首次启动时会把该示例密码替换为 `.env` 中的 `IEMES_ADMIN_PASSWORD`；实际登录用户名为 `admin`，当前实例密码只在本机 `.env` 中查看。不要用 SQL 注释中的示例密码登录 Docker 实例。

## 本地告警邮件

Compose 启动 Mailpit，收件箱在 `http://127.0.0.1:18025`。应用通过内部 SMTP 服务 `mailpit:1025` 发信，使用 `example.test` 虚拟收发地址，不向真实邮箱发送。首次初始化配置卷时自动写入虚拟地址；已存在的配置卷保留已有值。SMTP 主机、端口通过 `IEMES_SMTP_HOST`、`IEMES_SMTP_PORT` 注入，非容器环境默认 `smtp.163.com:25`。本机 `.env` 可以用 `IEMES_MAILPIT_IMAGE` 覆盖 Mailpit 镜像来源；默认 `axllent/mailpit:v1.32.0`。

2026-11-05 在 ARM64 Docker 中临时将 CPU、JVM、内存告警阈值设为 0，定时任务发送 1 封邮件并写入 1 条 `ly_server_info`，随后恢复原阈值 100/100/100。

## 本地备份与恢复

以下命令在项目根目录执行。`backups/` 被 Git 忽略；备份含数据库及配置内容，应只保存在可信的本机目录。目录参数按实际日期或批次替换。为取得一致的数据库与上传文件快照，先暂停应用写入；期间服务不可访问。如有其他写入客户端，也须先暂停。

```sh
umask 077
mkdir -m 700 -p backups/2026-11-05-restore-verified
docker compose stop app
docker compose exec -T db sh -c 'MYSQL_PWD="$MYSQL_PASSWORD" mysqldump -uiemes --single-transaction --no-tablespaces --routines --triggers --set-gtid-purged=OFF iemes_v1' > backups/2026-11-05-restore-verified/database.sql
docker compose run --rm --no-deps -T --entrypoint tar app -C /usr/local/tomcat/webapps/ROOT/uploadFile -cf - . > backups/2026-11-05-restore-verified/uploads.tar
docker compose run --rm --no-deps -T --entrypoint tar app -C /var/lib/iemes -cf - config.properties > backups/2026-11-05-restore-verified/configuration.tar
docker compose up -d --no-deps --wait app
```

若任一步失败，仍须执行最后的 `docker compose up -d --no-deps --wait app` 恢复服务，并丢弃不完整的备份目录。

恢复前先在独立 Compose 项目验证，不覆盖正在运行的 `ie-mes`：

```sh
docker compose -p ie-mes-restore-check up -d --no-build --wait db
docker compose -p ie-mes-restore-check exec -T db sh -c 'MYSQL_PWD="$MYSQL_PASSWORD" mysql -uiemes iemes_v1' < backups/2026-11-05-restore-verified/database.sql
docker compose -p ie-mes-restore-check run --rm --no-deps -T --entrypoint tar app -C /usr/local/tomcat/webapps/ROOT/uploadFile -xf - < backups/2026-11-05-restore-verified/uploads.tar
docker compose -p ie-mes-restore-check run --rm --no-deps -T --entrypoint tar app -C /var/lib/iemes -xf - < backups/2026-11-05-restore-verified/configuration.tar
docker compose -p ie-mes-restore-check run -d --rm --no-deps --name ie-mes-restore-check-app-probe app
docker inspect --format '{{.State.Health.Status}}' ie-mes-restore-check-app-probe
```

确认测试应用为 `healthy` 后，清理本次独立恢复环境：

```sh
docker stop ie-mes-restore-check-app-probe
docker compose -p ie-mes-restore-check down -v
```

`down -v` 只用于这个一次性测试项目，不能对正在使用的 `ie-mes` 项目执行。Mailpit 邮件历史不包含在业务备份中。

2026-11-05 已在隔离项目实测恢复：恢复前后 38 张表、9 个用户、111 个资源、12 张工单、1 条告警记录相符；上传卷 3 个文件和配置卷 1 个文件的 SHA-256 一致；恢复出的独立应用达到 `healthy`。另按上述暂停写入步骤创建 `backups/2026-11-05-quiesced-review/`：数据库导出完整，两个卷归档可读取，目录 0700、三个文件从创建起均为 0600，随后应用恢复 `healthy`。新备份尚未再次执行隔离恢复。
