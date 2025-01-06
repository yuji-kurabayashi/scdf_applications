#!/bin/sh -eu

usage() {
  cat <<EOUSAGE
Usage: $0 [< Options >]

  This shell script assumes that you are using the docker registry on wsl2 (Ubuntu) with the docker engine installed.
  If you want to use your docker hub registry, run this shell script below command.
  $0 -r your_docker_hub_user_name -u your_docker_hub_user_name -p your_docker_hub_user_password

  Options:
    -r: DOCKER_REGISTRY                : [optional] docker registry. (default is localhost:5000)
    -n: DOCKER_REPOSITORY_NAME         : [optional] docker repository name. (default is settings.gradle file's rootProject.name = 'value')
    -t: DOCKER_IMAGE_TAG               : [optional] docker image tag. (default is latest)
    -u: DOCKERHUB_USER_NAME            : [optional] docker hub user name.
                                         If you specified DOCKERHUB_USER_NAME and DOCKERHUB_PASSWORD, attempt to login to docker hub.
    -p: DOCKERHUB_PASSWORD             : [optional] docker hub user password.
                                         If you specified DOCKERHUB_USER_NAME and DOCKERHUB_PASSWORD, attempt to login to docker hub.
    -o: DOCKER_BUILD_OPTIONS           : [optional] docker build options. (default is --no-cache --load --platform linux/amd64)

EOUSAGE
}

DOCKER_REGISTRY=localhost:5000
DOCKER_REPOSITORY_NAME=
DOCKER_IMAGE_TAG=latest
DOCKERHUB_USER_NAME=
DOCKERHUB_PASSWORD=
DOCKER_BUILD_OPTIONS='--no-cache --load --platform linux/amd64'
# --load option cannot use multi platform build.
# ERROR: docker exporter does not currently support exporting manifest lists
#DOCKER_BUILD_OPTIONS='--no-cache --push --platform linux/amd64,linux/arm64/v8'
SETTINGS_GRADLE_FILE_PATH=./settings.gradle

while getopts r:n:t:u:p:o: OPT; do
  case ${OPT} in
    r) DOCKER_REGISTRY=${OPTARG} ;;
    n) DOCKER_REPOSITORY_NAME=${OPTARG} ;;
    t) DOCKER_IMAGE_TAG=${OPTARG} ;;
    u) DOCKERHUB_USER_NAME=${OPTARG} ;;
    p) DOCKERHUB_PASSWORD=${OPTARG} ;;
    o) DOCKER_BUILD_OPTIONS=${OPTARG} ;;
    *) usage; exit 1 ;;
  esac
done

if [ -n "$DOCKERHUB_USER_NAME" ] && [ -n "$DOCKERHUB_PASSWORD" ]; then
  (echo "$DOCKERHUB_PASSWORD" | docker login --username "$DOCKERHUB_USER_NAME"  --password-stdin) || {
    echo "Error: docker hub login failed"
    exit 1
  }
fi

if [ -z "$DOCKER_REPOSITORY_NAME" ]; then
  if [ -f "$SETTINGS_GRADLE_FILE_PATH" ]; then
    DOCKER_REPOSITORY_NAME=$(grep "rootProject.name" "$SETTINGS_GRADLE_FILE_PATH" | sed "s/.*'\\(.*\\)'.*/\\1/")
    if [ -z "$DOCKER_REPOSITORY_NAME" ]; then
      echo "Error: Could not determine DOCKER_REPOSITORY_NAME. Could not extract rootProject.name from $SETTINGS_GRADLE_FILE_PATH"
      exit 1
    fi
  else
    echo "Error: Could not determine DOCKER_REPOSITORY_NAME.  $SETTINGS_GRADLE_FILE_PATH not exists"
    exit 1
  fi
fi

DOCKER_REGISTRY_AND_REPOSITORY_AND_TAG=$DOCKER_REGISTRY/$DOCKER_REPOSITORY_NAME:$DOCKER_IMAGE_TAG

# In docker-registry, suddenly 'docker buildx build --push' does not work.
# For some reason, separating 'docker buildx build --load' and 'docker push' works.
docker buildx build $DOCKER_BUILD_OPTIONS -t "$DOCKER_REGISTRY_AND_REPOSITORY_AND_TAG" . || {
  echo "Error: docker build failed"
  exit 1
}

if echo "$DOCKER_BUILD_OPTIONS" | grep -q -- "--push"; then
  echo "Skipping docker push as --push is included in DOCKER_BUILD_OPTIONS"
else
  docker push "$DOCKER_REGISTRY_AND_REPOSITORY_AND_TAG" || {
    echo "Error: docker push failed"
    exit 1
  }
fi
