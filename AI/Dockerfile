FROM frolvlad/alpine-oraclejdk8:slim
# for time zone if we need it open these two lines.
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
VOLUME /tmp
ADD aicode.jar app.jar
RUN sh -c 'touch /app.jar'
# control memory size for jvm,if we need it open it.
ENV JAVA_OPTS="-Xms128m -Xmx512m"
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app.jar" ]
EXPOSE 8080