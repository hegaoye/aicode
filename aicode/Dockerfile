# 镜像如果不可用时 请更换
FROM amazoncorretto:21.0.11-alpine3.23
VOLUME /tmp
ADD aicode/build/libs/*.jar app.jar
RUN sh -c 'touch /app.jar'
ENV JAVA_OPTS="-Xms128m -Xmx512m"
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app.jar" ]
EXPOSE 8080
