# IE-MES

传统 Spring MVC / MyBatis / Shiro / JSP 的 Maven WAR 工程。

## 本机 Docker

```sh
# 首次使用：复制模板并设置数据库密码；已有 .env 时保留原文件。
cp .env.example .env
docker compose up -d --build --wait
```

浏览器打开 `http://localhost:18080/login.shtml`。

当前迁移工作区已生成本地 `.env`，无需再次复制覆盖。

部署细节与当前限制见 [Docker 部署说明](docs/migration/docker.md)。
功能迁移进度见 [验收清单](docs/migration/checklist.md)。Compose 配置通过不代表全部功能已验收。

## IntelliJ IDEA

通过根目录 `pom.xml` 导入 Maven 项目，Project SDK 和 Maven Runner 选择 JDK 8；在 IDEA 的 Maven 设置中将 User settings file 指向 `docker/maven-settings.xml`，使用阿里云镜像。原 Eclipse 配置保留。
