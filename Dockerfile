FROM openjdk:21

COPY ./jar /home
WORKDIR /home

ENTRYPOINT ["java", "-jar", "bot.jar"]