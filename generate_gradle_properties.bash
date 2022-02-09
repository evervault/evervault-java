#!/bin/bash

# https://central.sonatype.org/publish/publish-gradle/#metadata-definition-and-upload

echo "signing.keyId=test" >> ${HOME}/gradle.properties
echo "signing.password=${2}" >> ${HOME}/gradle.properties
echo "signing.secretKeyRingFile=${3}" >> ${HOME}/gradle.properties
echo "ossrhUsername=${SONATYPE_USERNAME}" >> ${HOME}/gradle.properties
echo "ossrhPassword=${SONATYPE_PASSWORD}" >> ${HOME}/gradle.properties