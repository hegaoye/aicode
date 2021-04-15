# 镜像如果不可用时 请更换
#FROM java:8-alpine
#ENV TZ=Asia/Shanghai
#RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
#VOLUME /tmp
#ADD aicode/build/libs/*.jar app.jar
#RUN sh -c 'touch /app.jar'
#ENV JAVA_OPTS="-Xms128m -Xmx512m"
#ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app.jar" ]
#EXPOSE 8080

FROM gradle:jdk8
COPY . /home/gradle/project
WORKDIR /home/gradle/project
ENTRYPOINT ["gradle"]
CMD ["bootRepackage"]