FROM adoptopenjdk/openjdk11:alpine-slim
RUN apk update && apk add --no-cache docker-cli
ENV DOCKER_TLS_CERTDIR ""
ENV DOCKER_DRIVER overlay2
ENV DOCKER_HOST tcp://docker:2375

