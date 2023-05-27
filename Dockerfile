FROM gradle:7-jdk17 as build
WORKDIR /retry
COPY . .
RUN ./gradlew build --stacktrace 

FROM openjdk:17
WORKDIR /retry
EXPOSE 80
COPY --from=build /retry/build/libs/backend-0.0.1-SNAPSHOT.jar .
CMD java -jar backend-0.0.1-SNAPSHOT.jar