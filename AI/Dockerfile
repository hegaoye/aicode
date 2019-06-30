FROM frolvlad/alpine-oraclejdk8:slim
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
VOLUME /tmp
ADD AI.war /usr/local/tomcat/webapps/ROOT.war
ENV JAVA_OPTS="-Xms128m -Xmx512m"
EXPOSE 8080