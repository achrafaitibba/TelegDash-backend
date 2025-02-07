# Stage 1: Build the application with Maven
FROM maven:3.8.7-openjdk-18 AS maven-builder
WORKDIR /opt/app
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline
COPY ./src ./src
RUN ./mvnw clean install -DskipTests

# Stage 2: Build the minimal JRE using Eclipse Temurin JDK
FROM eclipse-temurin:17-alpine AS jre-builder
ARG JAR_FILE_NAME
WORKDIR /opt/jre
COPY --from=maven-builder /opt/app/target/${JAR_FILE_NAME}.jar /opt/app/${JAR_FILE_NAME}.jar
RUN mkdir /opt/unzip && cd /opt/unzip && unzip /opt/app/${JAR_FILE_NAME}.jar
# Use jdeps to find required modules with correct class path
RUN jdeps \
    --ignore-missing-deps \
    --print-module-deps \
    --multi-release 17 \
    --class-path "/opt/unzip/BOOT-INF/lib/*" \
    --module-path "/opt/unzip/BOOT-INF/lib/*" \
    /opt/unzip/BOOT-INF/classes \
    > /opt/module-list.txt
# Build minimal JRE using the module list
RUN jlink \
    --module-path "$JAVA_HOME/jmods" \
    --add-modules $(cat /opt/module-list.txt) \
    --strip-debug \
    --compress 2 \
    --no-header-files \
    --no-man-pages \
    --output /opt/jre-minimal
# Clean up the unzipped files
RUN rm -Rf /opt/unzip

# Stage 3: Final image with minimal JRE and application (Alpine with glibc)
FROM alpine:3.18
ARG JAR_FILE_NAME
ENV JAR_FILE_NAME=${JAR_FILE_NAME}
WORKDIR /opt/app
RUN apk add --no-cache curl
#To follow Docker security best practices by running your application as a non-root user.
#RUN addgroup -S appgroup && adduser -S appuser -G appgroup
#USER appuser
ENV JAVA_HOME=/opt/jre-minimal
ENV PATH="$PATH:$JAVA_HOME/bin"
COPY --from=jre-builder /opt/jre-minimal /opt/jre-minimal
COPY --from=maven-builder /opt/app/target/${JAR_FILE_NAME}.jar /opt/app/${JAR_FILE_NAME}.jar
EXPOSE 8080
ENTRYPOINT java -Dspring.profiles.active=prod -jar /opt/app/$JAR_FILE_NAME.jar
## To find needed modules
#mkdir modules
#cd ./modules
#unzip ../PlanSmart.jar
#cd ..
#jdeps --print-module-deps --ignore-missing-deps --recursive --multi-release 17 --class-path="./modules/BOOT-INF/lib/*" --module-path="./modules/BOOT-INF/lib/*" ./target/PlanSmart.jar
#rm -Rf ./modules
########################
#Or just use : --add-modules ALL-MODULE-PATH \

# jdeps --list-deps