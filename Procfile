default_process_types:
  web: with_jmap java -Dspring.profiles.active=production -Djava.security.egd=file:/dev/./urandom -XX:+PrintGCDetails -XX:+PrintGCDateStamps -XX:+PrintTenuringDistribution -XX:+UseConcMarkSweepGC -jar build/libs/machinosoccerweb.jar --server.port=$PORT
