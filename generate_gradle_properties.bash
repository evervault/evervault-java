#!/bin/bash

# https://central.sonatype.org/publish/publish-gradle/#metadata-definition-and-upload

# create gpg key file
echo ${GPG_KEY_FILE} | base64 -d > ${HOME}/gpg_key_file.gpg

echo "signing.keyId=${GPG_KEY_ID}" >> ${HOME}/.gradle/gradle.properties
echo "signing.password=${GPG_KEY_PASSWORD}" >> ${HOME}/.gradle/gradle.properties
echo "signing.secretKeyRingFile=${HOME}/gpg_key_file.gpg" >> ${HOME}/.gradle/gradle.properties
echo "ossrhUsername=${SONATYPE_USERNAME}" >> ${HOME}/.gradle/gradle.properties
echo "ossrhPassword=${SONATYPE_PASSWORD}" >> ${HOME}/.gradle/gradle.properties