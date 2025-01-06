ARG BASE_BUILD_IMAGE=eclipse-temurin
ARG BASE_BUILD_IMAGE_TAG=21.0.5_11-jdk-alpine
ARG BASE_RUNTIME_IMAGE=eclipse-temurin
ARG BASE_RUNTIME_IMAGE_TAG=21.0.5_11-jre-alpine
ARG TEST_DIR=src/test
ARG TIMEZONE=Asia/Tokyo

FROM ${BASE_BUILD_IMAGE}:${BASE_BUILD_IMAGE_TAG} AS build_stage
WORKDIR /app/source
COPY . .
RUN <<EOF
  set -e
  if [ -x "$(command -v apk)" ]; then
    # for Alpine
    apk --no-cache add findutils
  elif [ -x "$(command -v apt)" ]; then
    # for Ubuntu/Debian
    apt update && apt install -y findutils
  elif [ -x "$(command -v dnf)" ]; then
    # for RHEL/CentOS 8+
    dnf install -y findutils
  elif [ -x "$(command -v yum)" ]; then
    # for CentOS 7-
    yum install -y findutils
  else
    echo "Unsupported package manager. Please install findutils manually."
    exit 1
  fi

# To keep the application file size small, and for security reasons,
# the source code for testing should be excluded and built.
  rm -rf ${TEST_DIR}

# Since Spring Boot 2.5, two types of jars are generated at build time:
# a plain jar that contains no dependencies at all and a fat jar that contains all dependencies.
# Here, the jar with the largest file size is copied and adopted.
  if [ -f gradlew ]; then
    chmod +x gradlew && ./gradlew clean build -x test
    mkdir -p /build/jar
    find build/libs -name "*.jar" -exec cp {} /build/jar/ \;
    cd /build/jar
    ls -S *.jar | head -n 1 | xargs -I {} mv {} app.jar
  elif [ -f mvnw ]; then
    chmod +x mvnw && ./mvnw clean package -DskipTests
    mkdir -p /build/jar
    find target -name "*.jar" -exec cp {} /build/jar/ \;
    cd /build/jar
    ls -S *.jar | head -n 1 | xargs -I {} mv {} app.jar
  else
    echo "No gradlew or mvnw found. Please provide a valid build file."
    exit 1
  fi
EOF

FROM ${BASE_RUNTIME_IMAGE}:${BASE_RUNTIME_IMAGE_TAG}
COPY --from=build_stage /build/jar/app.jar app.jar
# Provide a restricted user to prevent the application from running with root privileges.
# If the time zone is not set, all implementations that handle time, such as output logs, will be handled in UTC.
# Therefore, the time zone is explicitly specified. (Default is Asia/Tokyo)
RUN <<EOF
  set -e
  if [ -x "$(command -v apk)" ]; then
    # for Alpine
    apk --update-cache add tzdata shadow
    ln -sf /usr/share/zoneinfo/${TIMEZONE} /etc/localtime
    echo "${TIMEZONE}" > /etc/timezone
    addgroup -S executeUserGroup
    adduser -S executeUser -G executeUserGroup
  elif [ -x "$(command -v apt)" ]; then
    # for Ubuntu/Debian
    apt update && apt install -y tzdata passwd
    ln -sf /usr/share/zoneinfo/${TIMEZONE} /etc/localtime
    echo "${TIMEZONE}" > /etc/timezone
    groupadd -r executeUserGroup
    useradd -r -g executeUserGroup executeUser
  elif [ -x "$(command -v dnf)" ]; then
    # for RHEL/CentOS 8+
    dnf install -y tzdata shadow-utils
    ln -sf /usr/share/zoneinfo/${TIMEZONE} /etc/localtime
    echo "${TIMEZONE}" > /etc/timezone
    groupadd -r executeUserGroup
    useradd -r -g executeUserGroup executeUser
  elif [ -x "$(command -v yum)" ]; then
    # for CentOS 7-
    yum install -y tzdata shadow-utils
    ln -sf /usr/share/zoneinfo/${TIMEZONE} /etc/localtime
    echo "${TIMEZONE}" > /etc/timezone
    groupadd -r executeUserGroup
    useradd -r -g executeUserGroup executeUser
  else
    echo "Unsupported package manager. Please install tzdata and add users manually."
    exit 1
  fi
EOF
USER executeUser
ENTRYPOINT [ "java", "-jar", "app.jar" ]
