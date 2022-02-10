#!/bin/bash

# https://central.sonatype.org/publish/publish-gradle/#metadata-definition-and-upload

# create gpg key file
echo ${GPG_KEY_FILE} > ${HOME}/gpg_key_file.key

if [ ! -d ${HOME}/.gradle ]; then
  mkdir -p ${HOME}/.gradle
fi

echo "signing.keyId=${GPG_KEY_ID}" >> ${HOME}/.gradle/gradle.properties
echo "signing.password=${GPG_KEY_PASSWORD}" >> ${HOME}/.gradle/gradle.properties
echo "signing.secretKeyRingFile=${HOME}/gpg_key_file.key" >> ${HOME}/.gradle/gradle.properties
echo "ossrhUsername=${SONATYPE_USERNAME}" >> ${HOME}/.gradle/gradle.properties
echo "ossrhPassword=${SONATYPE_PASSWORD}" >> ${HOME}/.gradle/gradle.properties