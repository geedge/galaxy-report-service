ARG JDK_IMAGE
ARG GO_IMAGE
#FROM 192.168.40.153:9080/common/golang:1.15.6 as builder
FROM ${GO_IMAGE} as builder

WORKDIR /build
COPY xjar.go /build/

RUN CGO_ENABLED=0 GOOS=linux go build -a -installsuffix cgo -o xjar .

#FROM 192.168.40.153:9080/common/amazon-corretto:11.0.21.9.1
FROM ${JDK_IMAGE}
MAINTAINER Galaxy
VOLUME /tmp
WORKDIR /opt/saved-query-scheduler
COPY --from=builder /build .

COPY config config
ARG JAR_FILE
COPY ${JAR_FILE} saved-query-scheduler.xjar

#dockercompose set JAVA_OPTS
ENV JAVA_OPTS=" -Xms1024m -Xmx2048m "
ENV LANG=C.UTF-8
#ENV TZ=Asia/Almaty
#RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
ENTRYPOINT [ "sh", "-c", "./xjar java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom --add-opens java.base/jdk.internal.loader=ALL-UNNAMED --add-opens jdk.zipfs/jdk.nio.zipfs=ALL-UNNAMED -jar saved-query-scheduler.xjar" ]