#!/bin/bash

sudo apt update
sudo apt install openjdk-17-jdk

# install SDKMAN, recommended by gradle website
curl -s "https://get.sdkman.io" | bash

source "/root/.sdkman/bin/sdkman-init.sh"

sdk install gradle 7.3.3