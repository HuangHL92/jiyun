FROM openjdk:8-jdk-alpine
MAINTAINER jysoft Service@51e.com.cn
RUN apk add --no-cache tzdata bash ttf-dejavu fontconfig \
	&& fc-cache --force \
WORKDIR /app
ADD tysfrzpt-admin.jar /app/tysfrzpt-admin.jar
EXPOSE 8080
ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar", "/app/dataCenter-admin.jar"]