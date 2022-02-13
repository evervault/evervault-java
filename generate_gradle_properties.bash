#!/bin/bash

# https://central.sonatype.org/publish/publish-gradle/#metadata-definition-and-upload

set -e

if [ ! -d ${HOME}/.gradle ]; then
  mkdir -p ${HOME}/.gradle
fi

echo "signingKey=${GPG_KEY_FILE}" >> ${1}/gradle.properties
echo "signingPassword=${GPG_KEY_PASSWORD}" >> ${1}/gradle.properties
echo "ossrhUsername=${SONATYPE_USERNAME}" >> ${1}/gradle.properties
echo "ossrhPassword=${SONATYPE_PASSWORD}" >> ${1}/gradle.properties