ARG JDK_IMAGE
FROM ${JDK_IMAGE}
MAINTAINER Galaxy
VOLUME /tmp
WORKDIR /home/tsg/galaxy/galaxy-report-service
COPY config config
COPY xjar.go xjar.go
RUN go build xjar.go
ARG JAR_FILE
COPY ${JAR_FILE} galaxy-report-service.xjar
#dockercompose set JAVA_OPTS
ENV JAVA_OPTS=" -Xms2048m -Xmx2048m "
ENV LANG=en_US.UTF-8 LANGUAGE=en_US:en LC_ALL=en_US.UTF-8
#ENV TZ=Asia/Almaty
#RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
ENTRYPOINT [ "sh", "-c", "./xjar java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar galaxy-report-service.xjar" ]
