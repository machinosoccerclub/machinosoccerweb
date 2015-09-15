default_process_types:
  web: java -Dspring.profiles.active=production -Djava.security.egd=file:/dev/./urandom $JAVA_OPTS -XX:+PrintGCDetails -XX:+PrintHeapAtGC -XX:+PrintGCDateStamps -XX:+PrintTenuringDistribution -XX:+UseConcMarkSweepGC -jar build/libs/machinosoccerweb.jar --server.port=$PORT
