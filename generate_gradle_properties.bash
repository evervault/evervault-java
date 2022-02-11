#!/bin/bash

# https://central.sonatype.org/publish/publish-gradle/#metadata-definition-and-upload

set -e

gpg --version

# create gpg key file
echo ${GPG_KEY_FILE} > ${HOME}/gpg_key_file.key

md5sum ${HOME}/gpg_key_file.key

# importing key back gpg, so we can export it back to the expected format
gpg --batch --import ${HOME}/gpg_key_file.key

echo "PGP file imported"

if [ ! -d ${HOME}/.gradle ]; then
  mkdir -p ${HOME}/.gradle
fi

gpg --export-secret-keys ${GPG_KEY_ID} > gpg_key_file.gpg

echo "key exported in expected format" 

echo "signing.keyId=${GPG_KEY_ID}" >> ${HOME}/.gradle/gradle.properties
echo "signing.password=${GPG_KEY_PASSWORD}" >> ${HOME}/.gradle/gradle.properties
echo "signing.secretKeyRingFile=${HOME}/gpg_key_file.gpg" >> ${HOME}/.gradle/gradle.properties
echo "ossrhUsername=${SONATYPE_USERNAME}" >> ${HOME}/.gradle/gradle.properties
echo "ossrhPassword=${SONATYPE_PASSWORD}" >> ${HOME}/.gradle/gradle.properties