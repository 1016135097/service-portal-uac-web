# Pull base image  
FROM centos:7

RUN yum install -y java-1.8.0-openjdk

# Install tomcat7
RUN mkdir pkg
ADD ./pkg/apache-tomcat-8.0.35.tar.gz /pkg/apache-tomcat-8.0.35
RUN  mv /pkg/apache-tomcat-8.0.35/apache-tomcat-8.0.35 /opt/apache-tomcat-8.0.35 && ls /opt/apache-tomcat-8.0.35

#change config parameter
RUN sed -i '/\# OS/i JAVA_OPTS="$JAVA_OPTS -server -Xms512M -Xmx512M -XX:PermSize=64M -XX:MaxPermSize=128M  -XX:+UseConcMarkSweepGC -XX:ParallelGCThreads=8 -XX:+PrintCommandLineFlags -XX:+PrintGCDetails -XX:+UseCompressedOops -XX:-UseLargePagesIndividualAllocation -XX:+HeapDumpOnOutOfMemoryError" \n if [[ "$JAVA_OPTS" != *-Djava.security.egd=* ]]; then \n   JAVA_OPTS="$JAVA_OPTS -Djava.security.egd=file:/dev/./urandom" \n  fi'  /opt/apache-tomcat-8.0.35/bin/catalina.sh
RUN rm -fr /opt/apache-tomcat-8.0.35/webapps/*

#Install service-portal-uac-web
COPY ./build/libs/service-portal-uac-web.war /opt/apache-tomcat-8.0.35/webapps/service-portal-uac-web.war

ENV CATALINA_HOME /opt/apache-tomcat-8.0.35
ENV PATH $CATALINA_HOME/bin:$PATH
ENV JDBC_FILE /opt/apache-tomcat-8.0.35/webapps/service-portal-uac-web/WEB-INF/classes/context/jdbc.properties

COPY ./script/tomcat8.sh /etc/init.d/tomcat8
COPY ./script/service-portal-uac-web.sh /service-portal-uac-web.sh
RUN chmod 755 /etc/init.d/tomcat8 /*.sh && rm -fr /pkg

# Expose ports.  
EXPOSE 8080

# Define default command.  
CMD ["/service-portal-uac-web.sh"]