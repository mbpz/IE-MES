FROM maven:3.9.9-eclipse-temurin-8 AS build
WORKDIR /build
COPY docker/maven-settings.xml /build/settings.xml
COPY pom.xml .
COPY src ./src
RUN --mount=type=cache,id=iemes-maven,target=/root/.m2 mvn -B -s settings.xml -gs settings.xml package
RUN mkdir /unpacked && cd /unpacked && jar xf /build/target/IE-MES.war
COPY docker/ContainerSupport.java /support/ContainerSupport.java
RUN javac -d /support /support/ContainerSupport.java

FROM tomcat:9.0.113-jdk8-temurin
RUN rm -rf /usr/local/tomcat/webapps/*
COPY --from=build /unpacked/ /usr/local/tomcat/webapps/ROOT/
COPY --from=build /support/ContainerSupport.class /opt/iemes/ContainerSupport.class
COPY docker/entrypoint.sh /opt/iemes/entrypoint.sh
RUN chmod +x /opt/iemes/entrypoint.sh
EXPOSE 8080
ENTRYPOINT ["/opt/iemes/entrypoint.sh"]
CMD ["catalina.sh", "run"]
