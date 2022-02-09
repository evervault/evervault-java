#!/bin/bash

# https://central.sonatype.org/publish/publish-gradle/#metadata-definition-and-upload

# create gpg key file
echo ${GPG_KEY_FILE} | base64 -d > result > ${HOME}/gpg_key_file.gpg

echo "signing.keyId=${GPG_KEY_ID}" >> ${HOME}/gradle.properties
echo "signing.password=${GPG_KEY_PASSWORD}" >> ${HOME}/gradle.properties
echo "signing.secretKeyRingFile=${HOME}/gpg_key_file.gpg" >> ${HOME}/gradle.properties
echo "ossrhUsername=${SONATYPE_USERNAME}" >> ${HOME}/gradle.properties
echo "ossrhPassword=${SONATYPE_PASSWORD}" >> ${HOME}/gradle.properties