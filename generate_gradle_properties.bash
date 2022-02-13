#!/bin/bash

# https://central.sonatype.org/publish/publish-gradle/#metadata-definition-and-upload

set -e

if [ ! -d ${HOME}/.gradle ]; then
  mkdir -p ${HOME}/.gradle
fi

echo "signingKey=${GPG_KEY_FILE}" >> ${HOME}/.gradle/gradle.properties
echo "signingPassword=${GPG_KEY_PASSWORD}" >> ${HOME}/.gradle/gradle.properties
echo "ossrhUsername=${SONATYPE_USERNAME}" >> ${HOME}/.gradle/gradle.properties
echo "ossrhPassword=${SONATYPE_PASSWORD}" >> ${HOME}/.gradle/gradle.properties