#!/bin/sh

sed -i "s/jdbc.url=.*/jdbc.url=jdbc:mysql:\/\/${DB_HOST}\/${DB_NAME}?useUnicode=true\&characterEncoding=UTF-8/g" ${JDBC_FILE}
sed -i "s/jdbc.username=.*/jdbc.username=${DB_USRER}/g" ${JDBC_FILE}
sed -i "s/jdbc.password=.*/jdbc.password=${DB_PWD}/g" ${JDBC_FILE}

nohup /opt/apache-tomcat-8.0.35/bin/catalina.sh run >> /service-portal-uac-web.log