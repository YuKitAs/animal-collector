FROM openjdk:11

ADD build/libs/animal-collector.jar /

ENV JAVA_MEMORY_OPTS -XX:+UnlockExperimentalVMOptions -XX:MaxRAMFraction=2
ENV JAVA_OPTS=$JAVA_MEMORY_OPTS

ENTRYPOINT exec java $JAVA_OPTS -jar /animal-collector.jar

EXPOSE 8080