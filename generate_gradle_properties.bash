#!/bin/bash

# https://central.sonatype.org/publish/publish-gradle/#metadata-definition-and-upload

set -e

if [ ! -d ${HOME}/.gradle ]; then
  mkdir -p ${HOME}/.gradle
fi

echo "signingKey=${GPG_KEY_FILE}" >> ${GITHUB_WORKSPACE}/gradle.properties
echo "signingPassword=${GPG_KEY_PASSWORD}" >> ${GITHUB_WORKSPACE}/gradle.properties
echo "ossrhUsername=${SONATYPE_USERNAME}" >> ${GITHUB_WORKSPACE}/gradle.properties
echo "ossrhPassword=${SONATYPE_PASSWORD}" >> ${GITHUB_WORKSPACE}/gradle.properties